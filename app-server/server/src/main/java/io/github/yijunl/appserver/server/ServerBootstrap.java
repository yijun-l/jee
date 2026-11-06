package io.github.yijunl.appserver.server;

import io.github.yijunl.appserver.spec.HttpRequest;
import io.github.yijunl.appserver.spec.HttpResponse;
import io.github.yijunl.appserver.spec.HttpServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerBootstrap {

    private static final int PORT = 8080;

    private static final Map<String, HttpServlet> servletMapping = new ConcurrentHashMap<>();

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception{
        System.out.println("[Server] - is running...");

        String appOutputDir = "app/target/classes";
        String targetServletClass = "io.github.yijunl.appserver.app.UserServlet";
        String contextPath = "/user";

        WebAppClassLoader appClassLoader = new WebAppClassLoader(appOutputDir);
        Class<?> clazz = appClassLoader.loadClass(targetServletClass);

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        HttpServlet servletInstance = (HttpServlet) constructor.newInstance();

        servletInstance.init();

        servletMapping.put(contextPath, servletInstance);
        System.out.println("[Server] successfully load and deploy: " + contextPath + " -> " + targetServletClass);

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("\n[Server] is shutting down...");
            servletMapping.values().forEach(HttpServlet::destroy);
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("[Server] Port is listening: http://localhost:" + PORT + contextPath);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(()-> handleRequest(clientSocket));
            }
        }

    }

    private static void handleRequest(Socket socket) {
        try (socket) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.isBlank()) return;

            String[] parts = firstLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            HttpRequest request = new HttpRequest(method, path);
            HttpResponse response = new HttpResponse(socket.getOutputStream());

            HttpServlet targetServlet = servletMapping.get(path);
            if (targetServlet != null){
                targetServlet.service(request, response);
            } else {
                response.setStatus(404);
                response.writeHtml("<h1>404 Not Found</h1><p>No Servlet mapped to: " + path + "</p>");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
