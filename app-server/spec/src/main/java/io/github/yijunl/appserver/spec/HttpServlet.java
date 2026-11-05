package io.github.yijunl.appserver.spec;

public abstract class HttpServlet {

    public void init() {
    }

    public void service(HttpRequest req, HttpResponse resp) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            doGet(req, resp);
        } else if ("POST".equalsIgnoreCase(req.getMethod())) {
            doPost(req, resp);
        } else {
            resp.setStatus(405);
            resp.writeHtml("<h1>405 Method Not Allowed</h1>");
        }
    }

    protected void doGet(HttpRequest req, HttpResponse resp) throws Exception {
        resp.setStatus(405);
        resp.writeHtml("<h1>GET not supported</h1>");
    }

    protected void doPost(HttpRequest req, HttpResponse resp) throws Exception {
        resp.setStatus(405);
        resp.writeHtml("<h1>POST not supported</h1>");
    }

    public void destroy() {
    }

}
