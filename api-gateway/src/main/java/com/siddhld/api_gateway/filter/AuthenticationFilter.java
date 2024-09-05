package com.siddhld.api_gateway.filter;

import com.siddhld.api_gateway.client.AuthServiceClient;
import com.siddhld.api_gateway.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//// Any Request that will Come VIA gateway should have to pass through this Security Filter first.
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final AuthServiceClient authServiceClient;
    private final JwtUtils jwtUtils;

    public AuthenticationFilter(RouteValidator validator, AuthServiceClient authServiceClient, JwtUtils jwtUtils) {
        super(Config.class);
        this.validator = validator;
        this.authServiceClient = authServiceClient;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                final String token = authHeader;
                // Validate via Auth-Service
//                return authServiceClient.validateToken(token)
//                        .flatMap(isValid -> {
//                            if (isValid) {
//                                return chain.filter(exchange);
//                            } else {
//                                return onError(exchange, "Unauthorized access to application", HttpStatus.UNAUTHORIZED);
//                            }
//                        })
//                        .onErrorResume(e -> onError(exchange, "Error validating token", HttpStatus.INTERNAL_SERVER_ERROR));


                final boolean isValid = jwtUtils.validateToken(token);
                if (isValid) {
                    return chain.filter(exchange);
                } else {
                    return onError(exchange, "Error validating token", HttpStatus.FORBIDDEN);
                }

            }
            return chain.filter(exchange);
        };
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}