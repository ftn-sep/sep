package org.sep.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
public class PccService {

    private final WebClient.Builder webClientBuilder;

    public String pingIssuer() {
        return webClientBuilder.build().get()
                .uri("http://issuer-service/api/issuer/card-payment")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
