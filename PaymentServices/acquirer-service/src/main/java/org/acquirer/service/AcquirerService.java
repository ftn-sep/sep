package org.acquirer.service;

import lombok.RequiredArgsConstructor;
import org.acquirer.dto.*;
import org.acquirer.model.BankAccount;
import org.acquirer.model.BankBin;
import org.acquirer.model.Payment;
import org.acquirer.repository.BankAccountRepository;
import org.acquirer.repository.BankBinRepository;
import org.acquirer.repository.PaymentRepository;
import org.sep.dto.card.CardDetails;
import org.sep.dto.card.IssuerBankPaymentResponse;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.enums.PaymentStatus;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final TransactionDetailsService transactionDetailsService;
    private final BankBinRepository bankBinRepository;

    private static final String CARD_DETAILS_PAGE = "http://localhost:4200/acquirer-bank/card-details";
    private static final int PAYMENT_LINK_DURATION_MINUTES = 15;
    private static final int PAN_BIN_LENGTH = 6;


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


    public PaymentResultResponse cardDetailsPayment(CardDetails paymentRequest) {

        Payment payment = paymentRepository.findById(paymentRequest.getPaymentId())
                .orElseThrow(() -> new NotFoundException("Payment doesn't exist!"));

        validatePayment(paymentRequest, payment);

        BankAccount sellerBankAcc = bankAccountRepository.findByMerchantId(payment.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Seller's bank account doesn't exist in acquire bank"));
        payment.setAcquirerAccountNumber(sellerBankAcc.getAccountNumber());

        IssuerBankPaymentResponse issuerBankResponse = null;

        if (isAccountsInTheSameBank(paymentRequest)) {
            sameBankPaymentService.doPayment(payment, paymentRequest, sellerBankAcc);
        } else {
            issuerBankResponse = twoBanksPaymentService.doPayment(payment, paymentRequest, sellerBankAcc);
        }

        transactionDetailsService.onSuccessPayment(payment, issuerBankResponse);

        return new PaymentResultResponse(payment.getSuccessUrl());
    }

    private boolean isAccountsInTheSameBank(CardDetails paymentRequest) {
        String bin = paymentRequest.getPan().substring(0, PAN_BIN_LENGTH);
        BankBin bankBin = bankBinRepository.findByBin(bin).orElse(null);
        return bankBin != null;
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


    private void validatePayment(CardDetails paymentRequest, Payment payment) {
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

    public SellersBankInformationDto getMerchantInfo(String accountNumber) {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new NotFoundException("Bank account with given account number: " + accountNumber + " doesn't exist")
        );

        if (bankAccount.getMerchantId() == null || bankAccount.getMerchantPassword() == null) {
            createMerchantIdAndPassword(bankAccount);
        }

        return SellersBankInformationDto.builder()
                .merchantId(bankAccount.getMerchantId())
                .merchantPassword(bankAccount.getMerchantPassword())
                .build();
    }

    private void createMerchantIdAndPassword(BankAccount bankAccount) {
        bankAccount.setMerchantId(UUID.randomUUID().toString().substring(0, 20));
        bankAccount.setMerchantPassword(UUID.randomUUID().toString().replace("-", "").substring(0, 25));
        bankAccountRepository.save(bankAccount);
    }
}


