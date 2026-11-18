package io.github.yijunl.appserver.app;

import io.github.yijunl.appserver.spec.HttpRequest;
import io.github.yijunl.appserver.spec.HttpResponse;
import io.github.yijunl.appserver.spec.HttpServlet;

public class UserServlet extends HttpServlet{
    @Override
    public void init(){
        System.out.println("[UserServlet] - invoke init() for initialization...");
    }

    @Override
    protected void doGet(HttpRequest req, HttpResponse resp) throws Exception{
        resp.writeHtml("<h1>Hello from UserServlet!</h1><p>Path: " + req.getPath() + "</p>");
    }

    @Override
    public void destroy(){
        System.out.println("[UserServlet] - invoke destroy() for resource release...");
    }
}
