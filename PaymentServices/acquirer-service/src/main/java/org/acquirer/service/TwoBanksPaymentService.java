package org.acquirer.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.repository.BankAccountRepository;
import org.apache.http.HttpHeaders;
import org.sep.dto.card.AcquirerToIssuerPaymentRequest;
import org.sep.dto.card.CardDetails;
import org.sep.dto.card.IssuerBankPaymentResponse;
import org.sep.enums.PaymentStatus;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.NotFoundException;
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

    public IssuerBankPaymentResponse doPayment(Payment payment, CardDetails cardDetails, BankAccount sellerBankAcc) {

        AcquirerToIssuerPaymentRequest paymentRequest = buildPaymentRequest(payment, cardDetails, sellerBankAcc);

        IssuerBankPaymentResponse issuerBankPaymentResponse = webClientBuilder.build().post()
                .uri("http://pcc-service/api/pcc/payment-card-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentRequest), AcquirerToIssuerPaymentRequest.class)
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

    private AcquirerToIssuerPaymentRequest buildPaymentRequest(Payment payment, CardDetails cardDetails,
                                                               BankAccount sellerBankAcc) {
        return AcquirerToIssuerPaymentRequest.builder()
                .cardDetails(cardDetails)
                .acquirerOrderId(generateOrderId())
                .acquirerTimeStamp(LocalDateTime.now())
                .amount(payment.getAmount())
                .acquirerAccountNumber(sellerBankAcc.getAccountNumber())
                .paymentId(payment.getId())
                .build();
    }

    private static long generateOrderId() {
        return ThreadLocalRandom.current().nextLong(1000000000);
    }

}

