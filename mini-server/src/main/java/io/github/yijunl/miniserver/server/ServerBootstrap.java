package io.github.yijunl.miniserver.server;

import io.github.yijunl.miniserver.app.AuthFilter;
import io.github.yijunl.miniserver.app.LogFilter;
import io.github.yijunl.miniserver.spec.HttpServlet;
import io.github.yijunl.miniserver.spec.Filter;

public class ServerBootstrap {
    public static void main(String[] args) throws Exception {

        String targetServletClass = "io.github.yijunl.miniserver.app.UserServlet";
        String contextPath = "/user";

        System.out.println("[Server] - Received request for path: " + contextPath);

        Class<?> clazz = Class.forName(targetServletClass);
        HttpServlet servlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
        System.out.println("[Server] - Instantiated component via reflection: " + servlet.getClass().getName());

        ApplicationFilterChain chain = new ApplicationFilterChain();
        chain.setServlet(servlet);

        String[] filterClasses = {
                "io.github.yijunl.miniserver.app.LogFilter",
                "io.github.yijunl.miniserver.app.AuthFilter"
        };

        for (String filterClass : filterClasses){
            Class<?> filterClazz = Class.forName(filterClass);
            Filter filter = (Filter) filterClazz.getDeclaredConstructor().newInstance();
            chain.addFilter(filter);
            System.out.println("[Server] - Register Filter " + servlet.getClass().getName() + "component via reflection");
        }

        chain.doFilter();
    }
}
