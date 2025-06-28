package com.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component//this filter is responsible for only allowing access from the gateway microservice, if you try to connect directly to the microservice, an error will be sent
public class GatewayAuthFilter extends OncePerRequestFilter {
    @Value("${gateway.auth.header:X-GATEWAY-SECRET}")
    private String headerName;
    @Value("${gateway.auth.value:key-microservices}")
    private String expectedValue;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(shouldSkip(path)){
            filterChain.doFilter(request,response);
            return;
        }

        String header = request.getHeader(headerName);
        if (!expectedValue.equals(header)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Access denied:Missing or invalid gateway header");
            return;
        }
        filterChain.doFilter(request, response);
    }
    private boolean shouldSkip(String path){//Este metodo permite eximir de un header de filtro a estos paths que se conectan con keycloak, ya que un header que no conozca keycloak se rechaza la conexion
        return path.matches("^/realms/.*/protocol/openid-connect/.*")
                || path.startsWith("/actuator/");
    }
}
