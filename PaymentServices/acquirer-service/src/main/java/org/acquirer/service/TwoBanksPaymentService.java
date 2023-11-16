package org.acquirer.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.acquirer.dto.CardDetailsPaymentRequest;
import org.acquirer.dto.IssuerBankPaymentRequest;
import org.acquirer.dto.IssuerBankPaymentResponse;
import org.acquirer.exception.BadRequestException;
import org.acquirer.exception.NotFoundException;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.model.enums.PaymentStatus;
import org.acquirer.repository.BankAccountRepository;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class TwoBanksPaymentService {

    private final WebClient.Builder webClientBuilder;
    private final TransactionDetailsService transactionDetailsService;
    private final BankAccountRepository bankAccountRepository;

    public IssuerBankPaymentResponse doPayment(Payment payment, CardDetailsPaymentRequest cardDetails, BankAccount sellerBankAcc) {

        IssuerBankPaymentRequest paymentRequest = IssuerBankPaymentRequest.builder()
                .cardDetails(cardDetails)
                .acquirerOrderId(generateOrderId())
                .acquirerTimeStamp(LocalDateTime.now())
                .amount(payment.getAmount())
                .acquirerAccountNumber(sellerBankAcc.getAccountNumber())
                .build();

        IssuerBankPaymentResponse issuerBankPaymentResponse = webClientBuilder.build().post()
                .uri("http://pcc-service/api/pcc/payment-card-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentRequest), IssuerBankPaymentRequest.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> response.bodyToMono(String.class).map(BadRequestException::new))
                .bodyToMono(IssuerBankPaymentResponse.class)
                .block();

        payment.setIssuerAccountNumber(issuerBankPaymentResponse.getIssuerAccountNumber());

        if (issuerBankPaymentResponse.getPaymentStatus() == PaymentStatus.FAILED) {
            transactionDetailsService.onFailedPayment(payment);
        } else if (issuerBankPaymentResponse.getPaymentStatus() == PaymentStatus.ERROR) {
            transactionDetailsService.onErrorPayment(payment);
        } else {
            sellerBankAcc.setBalance(sellerBankAcc.getBalance() + payment.getAmount());
            bankAccountRepository.save(sellerBankAcc);
        }
        return issuerBankPaymentResponse;

    }

    private static long generateOrderId() {
        return ThreadLocalRandom.current().nextLong(1000000000);
    }

}

