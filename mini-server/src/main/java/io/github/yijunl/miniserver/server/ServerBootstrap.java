package io.github.yijunl.miniserver.server;

import io.github.yijunl.miniserver.spec.HttpServlet;

public class ServerBootstrap {
    public static void main(String[] args) throws Exception {

        String targetServletClass = "io.github.yijunl.miniserver.app.UserServlet";
        String contextPath = "/user";

        System.out.println("[Server] - Received request for path: " + contextPath);

        Class<?> clazz = Class.forName(targetServletClass);
        HttpServlet servlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
        System.out.println("[Server] - Instantiated component via reflection: " + servlet.getClass().getName());

        System.out.println("[Server] - Invoking service() method...");
        servlet.service();
    }
}
