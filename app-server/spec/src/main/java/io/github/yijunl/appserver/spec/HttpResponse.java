package io.github.yijunl.appserver.spec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private final OutputStream out;
    private int status = 200;

    public HttpResponse(OutputStream out){
        this.out = out;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public void writeHtml(String html) throws IOException {
        byte[] body = html.getBytes(StandardCharsets.UTF_8);
        String responseText = "HTTP/1.1 " + status + " OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(responseText.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

}
