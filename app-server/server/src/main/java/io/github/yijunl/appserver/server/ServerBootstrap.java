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
    private static final String APP_OUTPUT_DIR = "app/target/classes";

    private static final Map<String, String> urlMapping = new ConcurrentHashMap<>();
    private static final Map<String, HttpServlet> servletMapping = new ConcurrentHashMap<>();

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static final WebAppClassLoader appClassLoader = new WebAppClassLoader(APP_OUTPUT_DIR);

    public static void main(String[] args) throws Exception {
        System.out.println("[Server] - is running...");

        urlMapping.put("/user", "io.github.yijunl.appserver.app.UserServlet");
        urlMapping.put("/test", "io.github.yijunl.appserver.app.TestServlet");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Server] is shutting down...");
            servletMapping.values().forEach(HttpServlet::destroy);
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] Port is listening: http://localhost:" + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleRequest(clientSocket));
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

            String targetClass = urlMapping.get(path);
            if (targetClass == null) {
                response.setStatus(404);
                response.writeHtml("<h1>404 Not Found</h1><p>No Servlet mapped to: " + path + "</p>");
                return;
            }

            HttpServlet targetServlet = servletMapping.get(targetClass);

            if (targetServlet == null) {
                Class<?> clazz = appClassLoader.loadClass(targetClass);
                targetServlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
                System.out.println("[Server] successfully load: " + targetClass);
                servletMapping.put(targetClass, targetServlet);
                targetServlet.init();
            }

            targetServlet.service(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
