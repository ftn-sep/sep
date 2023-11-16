package org.sep.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
//import org.sep.dto.AcquirerBankPaymentRequest;
//import org.sep.dto.IssuerBankPaymentResponse;
//import org.sep.model.Payment;
//import org.sep.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class PccService {

    private final WebClient.Builder webClientBuilder;
   // private final PaymentRepository paymentRepository;

    public String pingIssuer() {
        return webClientBuilder.build().get()
                .uri("http://issuer-service/api/issuer/card-payment")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


//    public IssuerBankPaymentResponse cardPayment(AcquirerBankPaymentRequest paymentRequest) {
//
//           IssuerBankPaymentResponse issuerBankPaymentResponse = webClientBuilder.build().post()
//                .uri("http://issuer-service/api/issuer/payment-card-details")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                .body(Mono.just(paymentRequest), AcquirerBankPaymentRequest.class)
//                .retrieve()
//                .bodyToMono(IssuerBankPaymentResponse.class)
//                .block();
//
//           Payment payment = Payment.builder()
//                   .acuqirerOrderId(paymentRequest.getAcquirerOrderId())
//                   .amount(paymentRequest.getAmount())
//                   .status(issuerBankPaymentResponse.getPaymentStatus())
//                   .build();
//
//            paymentRepository.save(payment);
//
//            return issuerBankPaymentResponse;
//    }
}
