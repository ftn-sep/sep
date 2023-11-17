package org.acquirer.service;

import lombok.RequiredArgsConstructor;
import org.acquirer.dto.*;
import org.acquirer.exception.BadRequestException;
import org.acquirer.exception.NotFoundException;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.model.enums.PaymentStatus;
import org.acquirer.repository.BankAccountRepository;
import org.acquirer.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
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
    private final TransactionDetailsService transactionDetailsService;

    private static final String CARD_DETAILS_PAGE = "http://localhost:4200/acquirer-bank/card-details";
    private static final String NO_PAYMENT_PAGE = "http://localhost:4200/acquirer-bank/no-payment-page";
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

        return new PaymentUrlIdResponse(paymentUrl, payment.getId(), paymentRequest.getAmount());
    }


    public PaymentResultResponse cardDetailsPayment(CardDetailsPaymentRequest paymentRequest) {

        Payment payment = paymentRepository.findById(paymentRequest.getPaymentId())
                .orElseThrow(() -> new NotFoundException("Payment doesn't exist!")); // todo: default error page?

        validatePayment(paymentRequest, payment);

        BankAccount sellerBankAcc = bankAccountRepository.findByMerchantId(payment.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Seller's bank account doesn't exist in acquire bank"));
        payment.setAcquirerAccountNumber(sellerBankAcc.getAccountNumber());

        IssuerBankPaymentResponse issuerBankResponse = null;

        if (isAccountsInTheSameBank(paymentRequest, sellerBankAcc)) {
            sameBankPaymentService.doPayment(payment, paymentRequest, sellerBankAcc);
        } else {
            issuerBankResponse = twoBanksPaymentService.doPayment(payment, paymentRequest, sellerBankAcc);
        }

        transactionDetailsService.onSuccessPayment(payment, issuerBankResponse);

        return new PaymentResultResponse(payment.getSuccessUrl());
    }

    private boolean isAccountsInTheSameBank(CardDetailsPaymentRequest paymentRequest, BankAccount sellerBankAcc) {
        return paymentRequest.getPan().charAt(0) == sellerBankAcc.getCard().getPan().charAt(0);
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
            transactionDetailsService.onErrorPayment(payment);
        }

        if (!paymentRequest.getUuid().equals(payment.getUuid())) {
            transactionDetailsService.onErrorPayment(payment);
        }
    }
}


