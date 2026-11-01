package io.github.yijun_l;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class HelloBean implements Serializable {
    private String name;
    private String message;

    public void submit() {
        this.message = "Hello, " + (name == null || name.trim().isEmpty() ? "World" : name) + "!";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
