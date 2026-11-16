package io.github.yijunl.miniserver.app;

import io.github.yijunl.miniserver.spec.Filter;
import io.github.yijunl.miniserver.spec.FilterChain;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(FilterChain chain) {
        System.out.println("[Auth Filter] start");
        chain.doFilter();
        System.out.println("[Auth Filter] end");
    }
}
