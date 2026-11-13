package io.github.yijunl.helloservlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[Character Encoding Filter] Initialized");
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        System.out.println("[Character Encoding Filter] Destroyed");
        Filter.super.destroy();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("[Character Encoding Filter] Enter doFilter()");

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("[Character Encoding Filter] Intercept " + req.getMethod() + " " + req.getRequestURI());

        chain.doFilter(request, response);

        System.out.println("[Character Encoding Filter] Leave doFilter()");
    }

}
