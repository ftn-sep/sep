package org.sep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
public class PspService {

    private final WebClient.Builder webClientBuilder;

    public String pingAcquirerService(String text) {

        return webClientBuilder.build().get()
                .uri("http://acquirer-service/api/acquirer/hello",
                        uriBuilder -> uriBuilder.queryParam("text", text).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String pingPaypal() {
        return webClientBuilder.build().get()
                .uri("http://paypal-service/api/paypal/hello")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String pingCrypto() {
        return webClientBuilder.build().get()
                .uri("http://crypto-service/api/crypto/hello")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String pingCardPayment() {
        return webClientBuilder.build().get()
                .uri("http://acquirer-service/api/acquirer/card-payment")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String pingIssuer() {
        return webClientBuilder.build().get()
                .uri("http://pcc-service/api/pcc/pingIssuer")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
