package com.siddhld.api_gateway.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceClient {
    private final WebClient webClient;

    public AuthServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8099/auth").build();
    }

    public Mono<Boolean> validateToken(String token) {
        return webClient.get()
                .uri("/validate-token")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}