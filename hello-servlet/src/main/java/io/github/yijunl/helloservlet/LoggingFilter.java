package io.github.yijunl.helloservlet;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("[Logging Filter] Enter doFilter()");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        System.out.println("[Logging Filter] Request from: " + req.getRemoteAddr());

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[Logging Filter] Response Status: " + resp.getStatus() + " Tooke " + duration +"ms");

        System.out.println("[Logging Filter] Leave doFilter()");
    }

    @Override
    public void destroy() {
        System.out.println("[Logging Filter] Destroyed");
        Filter.super.destroy();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[Logging Filter] Initialized");
        Filter.super.init(filterConfig);
    }
}
