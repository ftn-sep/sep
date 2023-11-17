package org.pcc.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.pcc.model.Payment;
import org.pcc.repository.PaymentRepository;

import org.sep.dto.card.AcquirerToIssuerPaymentRequest;
import org.sep.dto.card.IssuerBankPaymentResponse;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.NotFoundException;
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

    public IssuerBankPaymentResponse cardPayment(AcquirerToIssuerPaymentRequest paymentRequest) {

        IssuerBankPaymentResponse issuerBankPaymentResponse = webClientBuilder.build().post()
                .uri("http://issuer-service/api/issuer/payment-card-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentRequest), AcquirerToIssuerPaymentRequest.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> response.bodyToMono(String.class).map(BadRequestException::new))
                .bodyToMono(IssuerBankPaymentResponse.class)
                .block();

        Payment payment = Payment.builder()
                .id(paymentRequest.getPaymentId())
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