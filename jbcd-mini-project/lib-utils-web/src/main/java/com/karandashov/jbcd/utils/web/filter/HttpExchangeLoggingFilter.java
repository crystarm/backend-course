package com.karandashov.jbcd.utils.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpExchangeLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpExchangeLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info(
                "--> Request: {} {} {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info(
                    "<-- Response: {} - {} {}",
                    response.getStatus(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }
}
