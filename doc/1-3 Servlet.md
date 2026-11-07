# Servlet

A **Servlet** is a Java component that runs inside a **Servlet Container** to process client requests and generate dynamic responses. It is defined by the **Servlet specification**, which is now part of **Jakarta EE**.

Servlet is one of the fundamental building blocks of Java web development. Technologies such as **JSP**, **JSF**, and **Spring MVC** build on top of the **Servlet API**. Even modern **Spring Boot** applications commonly use an embedded Servlet Container, such as Apache Tomcat, to handle HTTP requests.

## The Container and IoC

A standard Java application starts execution from a `main()` method. A Servlet application follows a different execution model. Developers do not normally create Servlet instances directly using `new MyServlet()`. Instead, the **Servlet Container manages their creation, lifecycle, and request processing**.

This is an example of **Inversion of Control (IoC)**: the container controls the application flow, while developers provide application-specific logic through predefined methods.

The Servlet Container (such as Apache Tomcat or Eclipse Jetty) is responsible for:

- Loading Servlet classes and creating Servlet instances.
- Managing Servlet initialization and destruction.
- Mapping incoming requests to the appropriate Servlet.
- Providing HTTP request and response objects.
- Managing concurrent request processing.

The typical request processing flow is:

1. A client sends an HTTP request to the server.
2. The server passes the request to the Servlet Container.
3. The container examines the request URL and finds the corresponding Servlet mapping.
4. The container invokes the Servlet to process the request.
5. The Servlet executes application logic, interacts with service or database layers, and generates the response.
6. The container sends the HTTP response back to the client.

## Servlet Registration

A Servlet must be registered with the container so that incoming requests can be mapped to it.

Traditionally, this registration is defined in the deployment descriptor file (`WEB-INF/web.xml`). This clearly separates the Java implementation from the URL routing configuration.

### The Servlet Class

The Java class defines the request handling logic without hardcoding any URL paths:

```java
package com.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().println("Hello, Servlet!");
    }
}
```

### Registration in `web.xml`

To make this Servlet accessible, register the class and associate it with a URL pattern in `web.xml`:

```xml
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         version="6.0">

    <!-- 1. Declare the Servlet class -->
    <servlet>
        <servlet-name>HelloServlet</servlet-name>
        <servlet-class>com.example.HelloServlet</servlet-class>
    </servlet>

    <!-- 2. Map the Servlet to a URL pattern -->
    <servlet-mapping>
        <servlet-name>HelloServlet</servlet-name>
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>

</web-app>
```

## The Servlet Lifecycle

The **Servlet container** manages the entire lifecycle of a Servlet instance through three core methods:

- **init():** Called once after the container creates the Servlet instance. It handles one-time initialization, such as reading configuration parameters or allocating shared resources.
- **service():** Called for each incoming request. The container passes two key objects to `HttpServlet`:
  - `HttpServletRequest`: Provides access to request headers, parameters, cookies, and the input stream.
  - `HttpServletResponse`: Allows setting response headers, status codes, and response data.

  For `HttpServlet`, the default `service()` implementation inspects the HTTP method and routes the request to the matching handler, such as `doGet()`, `doPost()`, `doPut()`, or `doDelete()`.
- **destroy():** Called once before the container removes the Servlet instance. It allows the Servlet to release resources, such as background threads and other application-managed resources.

## Servlet Threading Model

By default, the Servlet Container creates only one instance of each Servlet and uses a worker thread pool to handle concurrent requests.

Instead of creating a new thread for every request, the container assigns an available worker thread from its pool to execute the Servlet's `service()` method. As a result, multiple threads may execute methods on the same Servlet instance simultaneously.

Since the Servlet instance is shared across threads, instance variables may also be accessed and modified by multiple requests concurrently, potentially causing race conditions. To avoid this, request-specific data should be stored in local variables, which belong to individual method invocations and are not shared between threads.

## Request and Response

The Servlet API provides two main interfaces for HTTP communication: `HttpServletRequest` and `HttpServletResponse`. The container creates their concrete implementations dynamically for each request and passes them to the Servlet.

### **HttpServletRequest**

Represents the incoming HTTP request and provides methods to access its data.

Common methods include:

- `getParameter()`: Reads request parameters.
- `getHeader()`: Reads HTTP headers.
- `getCookies()`: Retrieves request cookies.
- `getInputStream()` / `getReader()`: Reads the request body.
- `getRequestURI()`: Retrieves the request URI.

### **HttpServletResponse**

Represents the HTTP response and provides methods to generate the response sent back to the client.

Common methods include:

- `setStatus()`: Sets the HTTP status code.
- `setHeader()`: Sets response headers.
- `setContentType()`: Sets the response content type.
- `getWriter()` / `getOutputStream()`: Writes response content.
- `sendRedirect()`: Redirects the client to another URL.

Together, these two interfaces provide the basic mechanism for handling HTTP communication in Servlet applications.

## Standard Deployment Unit: The WAR Structure

A Java web application needs a standard packaging format so that a Servlet Container can deploy and run it. Jakarta EE defines the **WAR (Web Application Archive)** format for this purpose.

A WAR file is essentially a ZIP archive with a predefined directory structure:

```text
my-app.war
│
├── index.html
├── css/
├── js/
│
└── WEB-INF/
    ├── web.xml
    │
    ├── classes/
    │   └── com/example/servlet/HelloServlet.class
    │
    └── lib/
        └── mysql-connector.jar
```

### Core Components of the WAR Layout

- **Public Root Directory**: Files outside `WEB-INF/`, such as HTML, CSS, JavaScript, and images, can generally be accessed directly through HTTP URLs.

- **The Protected WEB-INF/ Directory**: The Servlet specification requires the container to prevent direct HTTP access to resources under `WEB-INF/`.

  For example, requesting `/WEB-INF/web.xml` directly returns a "404 Not Found" response. This prevents clients from directly accessing deployment descriptors, application classes, and private resources.

- **web.xml (Deployment Descriptor)**: The deployment descriptor defines web application configuration, including Servlet mappings, Filter mappings, and Listener declarations. Since Servlet 3.0, annotations such as `@WebServlet`, `@WebFilter`, and `@WebListener` can be used to configure these components directly in Java code. Therefore, `web.xml` is optional for many applications.

- **WEB-INF/classes/**: Contains compiled application classes (`.class` files) and other application resources. The Servlet Container uses a web application classloader to load these classes and resources.

- **WEB-INF/lib/**: Contains third-party dependency JAR files required by the application. The container makes these libraries available to the web application at runtime.

This structure allows multiple web applications to run inside the same Servlet Container while maintaining their own application classes and dependencies.