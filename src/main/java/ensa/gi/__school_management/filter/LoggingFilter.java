package ensa.gi.__school_management.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        long startTime = System.currentTimeMillis();
        
        System.out.println("========== REQUEST ==========");
        System.out.println("Method: " + httpRequest.getMethod());
        System.out.println("URI: " + httpRequest.getRequestURI());
        System.out.println("Query: " + httpRequest.getQueryString());
        System.out.println("Remote IP: " + httpRequest.getRemoteAddr());
        
        chain.doFilter(request, response);
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Duration: " + duration + "ms");
        System.out.println("=============================");
    }

    @Override
    public void destroy() {
        System.out.println("LoggingFilter destroyed");
    }
}
