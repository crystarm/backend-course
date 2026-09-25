package com.karandashov;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public final class ProxyHeadersUtils {

    private ProxyHeadersUtils() {
    }

    public static MockHttpServletRequestBuilder withAuthenticatedUser(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder
                .header("X-Forwarded-User", "student")
                .header("X-Forwarded-Email", "student@example.com")
                .header("X-Forwarded-Preferred-Username", "student")
                .header("X-Forwarded-Groups", "students,authors");
    }
}
