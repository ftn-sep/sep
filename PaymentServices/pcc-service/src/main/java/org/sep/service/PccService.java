package org.sep.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.sep.dto.AcquirerBankPaymentRequest;
import org.sep.dto.IssuerBankPaymentResponse;
import org.sep.exception.BadRequestException;
import org.sep.exception.NotFoundException;
import org.sep.model.Payment;
import org.sep.model.enums.PaymentStatus;
import org.sep.repository.PaymentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class PccService {

    private final WebClient.Builder webClientBuilder;
    private final PaymentRepository paymentRepository;


    public String pingIssuer() {
        return webClientBuilder.build().get()
                .uri("http://issuer-service/api/issuer/card-payment")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public IssuerBankPaymentResponse cardPayment(AcquirerBankPaymentRequest paymentRequest) {

        IssuerBankPaymentResponse issuerBankPaymentResponse = webClientBuilder.build().post()
                .uri("http://issuer-service/api/issuer/payment-card-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentRequest), AcquirerBankPaymentRequest.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> response.bodyToMono(String.class).map(BadRequestException::new))
                .bodyToMono(IssuerBankPaymentResponse.class)
                .block();

        Payment payment = Payment.builder()
                .acquirerOrderId(paymentRequest.getAcquirerOrderId())
                .acquirerTimeStamp(paymentRequest.getAcquirerTimeStamp())
                .issuerOrderId(issuerBankPaymentResponse.getIssuerOrderId())
                .issuerTimeStamp(issuerBankPaymentResponse.getIssuerTimeStamp())
                .amount(paymentRequest.getAmount())
                .status(issuerBankPaymentResponse.getPaymentStatus())
                .build();

        paymentRepository.save(payment);

        return issuerBankPaymentResponse;
    }
}