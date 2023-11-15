package org.acquirer.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.acquirer.dto.CardDetailsPaymentRequest;
import org.acquirer.dto.IssuerBankPaymentRequest;
import org.acquirer.dto.IssuerBankPaymentResponse;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.model.enums.PaymentStatus;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class TwoBanksPaymentService {

    private final WebClient.Builder webClientBuilder;

    public IssuerBankPaymentResponse doPayment(Payment payment, CardDetailsPaymentRequest cardDetails, BankAccount sellerBankAcc) {

        IssuerBankPaymentRequest paymentRequest = IssuerBankPaymentRequest.builder()
                .cardDetails(cardDetails)
                .acquirerOrderId(generateOrderId())
                .acquirerTimeStamp(LocalDateTime.now())
                .amount(payment.getAmount())
                .build();

        IssuerBankPaymentResponse issuerBankPaymentResponse = webClientBuilder.build().post()
                .uri("http://pcc-service/api/pcc/payment-card-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentRequest), IssuerBankPaymentRequest.class)
                .retrieve()
                .bodyToMono(IssuerBankPaymentResponse.class)
                .block();

        if(!issuerBankPaymentResponse.getPaymentStatus().equals(PaymentStatus.DONE)){
            throw new BadRequestException("Something went wrong !");
        }

        sellerBankAcc.setBalance(sellerBankAcc.getBalance() + payment.getAmount());
        return issuerBankPaymentResponse;

    }
    private static long generateOrderId() {
        return ThreadLocalRandom.current().nextLong(1000000000);
    }

}

