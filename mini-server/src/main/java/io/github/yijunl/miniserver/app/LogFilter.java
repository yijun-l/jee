package io.github.yijunl.miniserver.app;

import io.github.yijunl.miniserver.spec.Filter;
import io.github.yijunl.miniserver.spec.FilterChain;

public class LogFilter implements Filter {
    @Override
    public void doFilter(FilterChain chain) {
        System.out.println("[Log Filter] start");
        chain.doFilter();
        System.out.println("[Log Filter] end");

    }
}
