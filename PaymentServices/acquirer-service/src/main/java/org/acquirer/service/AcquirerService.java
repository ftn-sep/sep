package org.acquirer.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.acquirer.dto.*;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.model.enums.PaymentStatus;
import org.acquirer.repository.BankAccountRepository;
import org.acquirer.repository.PaymentRepository;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AcquirerService {

    private final WebClient.Builder webClientBuilder;
    private final BankAccountRepository bankAccountRepository;
    private final PaymentRepository paymentRepository;
    private final SameBankPaymentService sameBankPaymentService;
    private final TwoBanksPaymentService twoBanksPaymentService;

    private static final String CARD_DETAILS_PAGE = "http://localhost:4200/acquirer-bank/card-details";
    private static final int PAYMENT_LINK_DURATION_MINUTES = 15;

    public String pingPcc() {
        return webClientBuilder.build().get()
                .uri("http://pcc-service/api/pcc/card-payment")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public PaymentUrlIdResponse generatePaymentUrlAndId(PaymentUrlAndIdRequest paymentRequest) {

        validatePaymentUrlRequest(paymentRequest);

        UUID uuid = UUID.randomUUID();
        Payment payment = paymentRepository.save(
                new Payment(uuid, paymentRequest, PAYMENT_LINK_DURATION_MINUTES));

        String paymentUrl = CARD_DETAILS_PAGE + "/" + uuid + "/" + payment.getId();

        return new PaymentUrlIdResponse(paymentUrl, payment.getId());
    }


    public PaymentResultResponse cardDetailsPayment(CardDetailsPaymentRequest paymentRequest) {

        Payment payment = paymentRepository.findById(paymentRequest.getPaymentId())
                .orElseThrow(() -> new NotFoundException("Payment doesn't exists!"));

        validatePayment(paymentRequest, payment);

        BankAccount sellerBankAcc = bankAccountRepository.findByMerchantId(payment.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Seller's bank account doesn't exits"));

        IssuerBankPaymentResponse issuerBankResponse = new IssuerBankPaymentResponse();

        if (paymentRequest.getPan().charAt(0) == sellerBankAcc.getCard().getPan().charAt(0)) {
            sameBankPaymentService.doPayment(payment, paymentRequest, sellerBankAcc);
        } else {
            issuerBankResponse = twoBanksPaymentService.doPayment(payment, paymentRequest, sellerBankAcc);
        }
        payment.setStatus(PaymentStatus.DONE);
        paymentRepository.save(payment);
        sendTransactionDetailsToPsp(payment, issuerBankResponse);
        List<String> urls = List.of(payment.getSuccessUrl(), payment.getFailedUrl(), payment.getErrorUrl());
        return new PaymentResultResponse(urls.get(payment.getStatus().ordinal()));
    }

    public void sendTransactionDetailsToPsp(Payment payment, IssuerBankPaymentResponse issuerBankResponse) {

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .merchantOrderId(payment.getMerchantOrderId())
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus())
                .acquirerOrderId(issuerBankResponse.getAcquirerOrderId())
                .acquirerTimestamp(issuerBankResponse.getAcquirerTimeStamp())
                .build();

        webClientBuilder.build().post()
                .uri("http://psp-service/api/psp/transaction-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDetails), TransactionDetails.class)
                .exchange().toFuture();
    }

    private void validatePaymentUrlRequest(PaymentUrlAndIdRequest paymentRequest) {
        BankAccount bankAccount = bankAccountRepository.findByMerchantId(paymentRequest.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Bank merchant id was not found"));

        Payment existingPaymentProcess = paymentRepository.findByMerchantOrderId(paymentRequest.getMerchantOrderId())
                .orElse(null);

        if (existingPaymentProcess != null) throw new BadRequestException("Payment process already in progress");

        if (!bankAccount.getMerchantPassword().equals(paymentRequest.getMerchantPassword())) {
            throw new BadRequestException("Merchant password is not correct");
        }
    }


    private void validatePayment(CardDetailsPaymentRequest paymentRequest, Payment payment) {
        if (payment.getStatus() != PaymentStatus.IN_PROGRESS) {
            throw new BadRequestException("Payment status is not in progress!");
        }

        if (payment.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Link is expired");
        }

        if (!paymentRequest.getUuid().equals(payment.getUuid())) {
            throw new BadRequestException("Tokens for payment doesn't match");
        }
    }
}


