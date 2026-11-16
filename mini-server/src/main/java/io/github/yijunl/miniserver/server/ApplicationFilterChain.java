package io.github.yijunl.miniserver.server;

import io.github.yijunl.miniserver.spec.Filter;
import io.github.yijunl.miniserver.spec.FilterChain;
import io.github.yijunl.miniserver.spec.HttpServlet;

import java.util.ArrayList;
import java.util.List;

public class ApplicationFilterChain implements FilterChain {
    private final List<Filter> filters = new ArrayList<>();
    private int pos = 0;
    private HttpServlet servlet;

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void doFilter() {
        if (pos < filters.size()) {
            Filter filter = filters.get(pos++);
            filter.doFilter(this);
        } else {
            if (servlet != null) {
                servlet.service();
            }
        }
    }
}
