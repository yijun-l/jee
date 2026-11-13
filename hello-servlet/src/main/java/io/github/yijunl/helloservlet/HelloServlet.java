package io.github.yijunl.helloservlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");

        String userAgent = request.getHeader("User-Agent");
        String language = request.getHeader("Accept-Language");
        String host = request.getHeader("Host");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<body>");

            out.println("<h2>Hello, " + name + "!</h2>");

            out.println("<p><b>Your Browser (User-Agent):</b> " + userAgent + "</p>");
            out.println("<p><b>Your Language:</b> " + language + "</p>");
            out.println("<p><b>Host:</b> " + host + "</p>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public void init() throws ServletException {
        System.out.println("[HelloServlet] Initialized");
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("[HelloServlet] service() - Dispatching()");
        super.service(req,resp);
    }

    @Override
    public void destroy() {
        System.out.println("[HelloServlet] Destroyed");
        super.destroy();
    }
}
