package io.github.yijunl.miniserver.app;


import io.github.yijunl.miniserver.spec.HttpServlet;

public class UserServlet extends HttpServlet {
    @Override
    public void service() {
        System.out.println("[App]    - Hello from App!");
    }
}
