package com.luzonni.cashflow.infrastructure.http.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestContextFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String ip = requestContext.getHeaderString("X-Forwarded-For");
        String userAgent = requestContext.getHeaderString("User-Agent");
        requestContext.setProperty("ip", ip);
        requestContext.setProperty("userAgent", userAgent);
    }

}
