package io.github.yijunl.appserver.app;

import io.github.yijunl.appserver.spec.HttpRequest;
import io.github.yijunl.appserver.spec.HttpResponse;
import io.github.yijunl.appserver.spec.HttpServlet;

public class UserServlet extends HttpServlet{
    @Override
    public void init(){
        System.out.println("[App] - invoke init() for initialization...");
    }

    @Override
    protected void doGet(HttpRequest req, HttpResponse resp) throws Exception{
        resp.writeHtml("<h1>Hello from App!</h1><p>Path: " + req.getPath() + "</p>");
    }

    @Override
    public void destroy(){
        System.out.println("[App] - invoke destroy() for resource release...");
    }
}
