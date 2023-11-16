package org.acquirer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
public class TwoBanksPaymentService {

    private final WebClient.Builder webClientBuilder;

//    public DifferentBankPaymentResponse doPayment(Payment payment, CardDetailsPaymentRequest cardDetails) {
//        long leftLimit = 1L;
//        long rightLimit = 10000000000L;
//        long acquirerOrderId = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
//
//        DifferentBankPaymentRequest paymentRequest = DifferentBankPaymentRequest.builder()
//                .cardDetails(cardDetails)
//                .acquirerOrderId(acquirerOrderId)
//                .acquirerTimeStamp(LocalDateTime.now())
//                .amount(payment.getAmount())
//                .build();
//
//        return webClientBuilder.build().post()
//                .uri("http://pcc-service/api/pcc/payment-card-details")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                .body(Mono.just(paymentRequest), DifferentBankPaymentRequest.class)
//                .retrieve()
//                .bodyToMono(DifferentBankPaymentResponse.class)
//                .block();
//    }
}
