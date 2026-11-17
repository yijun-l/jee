# Servlet Filter 

## The Classic Chain of Responsibility Pattern

The **Chain of Responsibility** is a behavioral design pattern that passes a request through a sequence of handlers. Each handler decides whether to process the request, pass it to the next handler, or stop the chain.

This keeps the sender separate from the objects that handle the request.

### Handlers Are Linked Together

In a classic implementation, each handler maintains a reference to the next handler in the chain:

```java
abstract class Handler {
    protected Handler next;
    
    public void setNext(Handler next) {
        this.next = next;
    }
    
    public abstract void handle(String request);
}
```

Concrete handlers can either process a request or pass it to the next handler:

```java
class AuthHandler extends Handler {
    @Override
    public void handler(String request) {
        if (request.contains("TOKEN")) {
            System.out.println("Auth passed");
            if (next != null) next.handle(request);
        } else {
            System.out.println("Auth failed. Request stopped");
        }
    }
}

class LogHandler extends Handler{
    @Override
    public void handler(String request){
        System.out.println("Logging: " + request);
        if (next != null) next.handle(request);
    }
}
```

### Limitations in Web Applications

Although this approach works well for simple scenarios, it introduces some limitations when used to build a flexible request-processing pipeline:

- **Handlers Are Tightly Coupled**: Every handler must hold a `next` reference and manually call `next.handle()`. A handler cannot be easily reused in different chains with different orders.
- **Limited Support for Post-Processing**: A simple `void handle(request)` method does not provide a standard mechanism for passing control back through the chain to process a response.
- **Requests Can Be Dropped**: If a request reaches the end of the chain without being handled, it is ignored silently.

These limitations make the classic implementation less convenient for web request processing, where multiple components need to intercept and process both incoming requests and outgoing responses.

## Servlet FilterChain Manages the Execution Flow

The Servlet API extends the Chain of Responsibility pattern with two interfaces: `Filter` and `FilterChain`.

Instead of requiring filters to maintain references to one another, the Servlet container manages the filter sequence. For each incoming request, the container selects the matching filters, builds an execution chain, and associates it with the target Servlet.

The `FilterChain` acts as a central coordinator. It maintains a list of filters and uses an internal cursor (`pos`) to track the next filter to execute.

When a filter calls `chain.doFilter()`, the chain advances to the next filter. Once all filters have been executed, the target Servlet is invoked.

### A Simplified Implementation

The following example illustrates the core execution mechanism used by Tomcat's `ApplicationFilterChain`.

#### Define the Core Interfaces

```java
interface Filter {
    void init();
    void doFilter(String request, String response, FilterChain chain);
    void destroy();
}

interface FilterChain {
    void doFilter(String request, String response);
}
```

#### Implement the Central Chain Manager

```java
class ApplicationFilterChain implements FilterChain {
    private final List<Filter> filters = new ArrayList<>();
    private Servlet servlet;
    private int pos = 0; // Cursor tracking the current filter

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void doFilter(String request, String response) {
        if (pos < filters.size()) {
            // Get the current filter and move the cursor forward
            Filter filter = filters.get(pos++);
            // Pass 'this' (the chain) so the filter can trigger the next step
            filter.doFilter(request, response, this);
        } else {
            // End of the chain: execute the target Servlet
            if (servlet != null) {
                servlet.service(request, response);
            }
        }
    }
}
```

The key operation is `pos++`. Each time `doFilter()` is called, the chain retrieves the next filter and advances the cursor before invoking it.

Passing `this` to the filter allows it to continue the execution by calling `chain.doFilter()`.


#### Implement a Filter

```java
class AuthFilter implements Filter {
    @Override
    public void init() {}

    @Override
    public void doFilter(String request, String response, FilterChain chain) {
        System.out.println("[AuthFilter] --> Request in: Checking authentication...");
        
        // Pass request to the next filter (or Servlet)
        chain.doFilter(request, response);
        
        System.out.println("[AuthFilter] <-- Response out: Cleaning up auth context");
    }

    @Override
    public void destroy() {}
}
```

When `AuthFilter` calls `chain.doFilter()`, execution moves to the next filter or the target Servlet. After that invocation returns, the remaining code in `AuthFilter` continues to execute.

### The Benefits of FilterChain

This design provides several advantages for web request processing:

- **Filters Stay Decoupled and Reusable**: Filters do not know about each other. They only call `chain.doFilter()`. The container can reuse the same filter instances in different orders for different URLs.
- **Two-Way Execution Flow**: Code before `chain.doFilter()` handles the incoming request, while code after it can process the response or clean up resources. This creates a nested execution flow similar to an onion.
- **Centralized Chain Management**: The container manages filter selection, ordering, and execution. If a filter does not call `chain.doFilter()`, it can intentionally stop the request before the target Servlet is reached.

One important detail is that the Servlet is invoked only when execution reaches the end of the chain. A filter can prevent this by choosing not to continue the chain.

## Implementing and Registering a Servlet Filter

In a traditional Java EE or Jakarta EE web application, implementing and registering a filter involves two main steps.

### Step 1: Implement the Filter Interface

A Servlet filter implements three lifecycle methods:

- `init()`: Called by the container when the filter is initialized.
- `doFilter()`: Called for each matching request.
- `destroy()`: Called when the container removes the filter from service.

Here is an example of a character encoding filter:

```java
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
```

### Step 2: Register the Filter in web.xml

The Servlet container needs to know which requests should pass through the filter. In a traditional deployment, this is configured in `WEB-INF/web.xml` using `<filter>` and `<filter-mapping>`:

```xml
   <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>io.github.yijunl.helloservlet.CharacterEncodingFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```