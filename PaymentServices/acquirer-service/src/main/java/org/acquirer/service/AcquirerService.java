package org.acquirer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.acquirer.dto.*;
import org.acquirer.dto.qrcode.QrCodeGenerateResponseDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AcquirerService {

    private final BankAccountRepository bankAccountRepository;
    private final PaymentRepository paymentRepository;
    private final SameBankPaymentService sameBankPaymentService;
    private final TwoBanksPaymentService twoBanksPaymentService;
    private final TransactionDetailsService transactionDetailsService;
    private final BankBinRepository bankBinRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String CARD_DETAILS_PAGE = "http://localhost:4200/acquirer-bank/card-details";
    private static final String QRCODE_GENERATE_URL = "https://nbs.rs/QRcode/api/qr/v1/generate";
    private static final String QR_CODE = "http://localhost:4200/qrcode-payment";
    private static final int PAYMENT_LINK_DURATION_MINUTES = 15;
    private static final int PAN_BIN_LENGTH = 6;

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

    public PaymentUrlIdResponse generateQRCode(PaymentUrlAndIdRequest paymentRequest) throws IOException, InterruptedException {
        var payload = String.format("K:PR|V:01|C:1|R:100000012345678940|N:AGENCIJA|I:RSD%d,00|SF:289|S:Plaćanje|RO:001234", (int)paymentRequest.getAmount()*60);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(QRCODE_GENERATE_URL))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        var httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        var orderResponse = objectMapper.readValue(content, QrCodeGenerateResponseDTO.class);
        if (!orderResponse.getS().getDesc().equals("OK.")) {
            throw new BadRequestException("Error while generating QR code");
        }

        //validatePaymentUrlRequest(paymentRequest);

        UUID uuid = UUID.randomUUID();
        Payment payment = paymentRepository.save(
                new Payment(uuid, paymentRequest, PAYMENT_LINK_DURATION_MINUTES));

        String paymentUrl = orderResponse.getI() + "|" + QR_CODE + "/" + uuid + "/" + payment.getId();

        PaymentUrlIdResponse paymentUrlIdResponse = new PaymentUrlIdResponse(paymentUrl, payment.getId(), paymentRequest.getAmount());

        return paymentUrlIdResponse;
    }
}


