package org.sep.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.sep.model.PaymentData;
import org.sep.model.dto.PaymentRequest;
import org.sep.repository.PspRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
public class PspService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    private final PspRepository pspRepository;

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

    public String savePayment(PaymentRequest paymentRequest){
        PaymentData paymentData = new PaymentData();
        paymentData.setAmount(paymentRequest.getAmount());
        paymentData.setMerchantOrderId(paymentRequest.getMerchantOrderId());
        paymentData.setMerchantTimeStamp(paymentRequest.getMerchantTimeStamp());
        pspRepository.save(paymentData);
        return "Ok !";
    }
}
