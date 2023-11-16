package org.sep.service;

import lombok.RequiredArgsConstructor;
import org.sep.dto.AcquirerBankPaymentRequest;
import org.sep.dto.IssuerBankPaymentResponse;
import org.sep.exception.BadRequestException;
import org.sep.exception.NotFoundException;
import org.sep.model.BankAccount;
import org.sep.model.Payment;
import org.sep.model.enums.PaymentStatus;
import org.sep.repository.BankAccountRepository;
import org.sep.repository.PaymentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class IssuerService {

    private final BankAccountRepository bankAccountRepository;
    private final PaymentRepository paymentRepository;

    public IssuerBankPaymentResponse cardPayment(AcquirerBankPaymentRequest paymentRequest) {

        validateCard(paymentRequest);

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getCardDetails().getPan())
                .orElseThrow(() -> new NotFoundException("Customer card doesn't exist !"));

        IssuerBankPaymentResponse issuerBankPaymentResponse = buildIssuerBankPaymentResponse(customerBankAcc, paymentRequest);

        if (customerBankAcc.getBalance() < paymentRequest.getAmount()) {
            issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.FAILED);
            return issuerBankPaymentResponse;
        }

        customerBankAcc.setBalance(customerBankAcc.getBalance() - paymentRequest.getAmount());
        issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.DONE);
        bankAccountRepository.save(customerBankAcc);

        Payment payment = buildPayment(issuerBankPaymentResponse, paymentRequest, customerBankAcc);
        paymentRepository.save(payment);

        return issuerBankPaymentResponse;
    }

    private Payment buildPayment(IssuerBankPaymentResponse issuerBankPaymentResponse, AcquirerBankPaymentRequest paymentRequest, BankAccount customerBankAcc) {
        return Payment.builder()
                .issuerTimestamp(issuerBankPaymentResponse.getIssuerTimeStamp())
                .issuerOrderId(issuerBankPaymentResponse.getIssuerOrderId())
                .status(issuerBankPaymentResponse.getPaymentStatus())
                .amount(paymentRequest.getAmount())
                .acquirerAccountNumber(paymentRequest.getAcquirerAccountNumber())
                .issuerAccountNumber(customerBankAcc.getAccountNumber())
                .acquirerTimestamp(paymentRequest.getAcquirerTimeStamp())
                .acquirerOrderId(paymentRequest.getAcquirerOrderId())
                .build();
    }

    private IssuerBankPaymentResponse buildIssuerBankPaymentResponse(BankAccount customerBankAcc, AcquirerBankPaymentRequest paymentRequest) {
        return IssuerBankPaymentResponse.builder()
                .issuerOrderId(generateOrderId())
                .issuerTimeStamp(LocalDateTime.now())
                .issuerAccountNumber(customerBankAcc.getAccountNumber())
                .acquirerOrderId(paymentRequest.getAcquirerOrderId())
                .acquirerTimeStamp(paymentRequest.getAcquirerTimeStamp())
                .build();
    }

    private static long generateOrderId() {
        return ThreadLocalRandom.current().nextLong(1000000000);
    }

    private void validateCard(AcquirerBankPaymentRequest paymentRequest) {
        checkIfAllParametersAreSame(paymentRequest);
        validateCardExpirationDate(paymentRequest);
    }

    private void validateCardExpirationDate(AcquirerBankPaymentRequest paymentRequest) {
        String[] date = paymentRequest.getCardDetails().getCardExpiresIn().split("/");
        LocalDate expirationDate = LocalDate.of(Integer.parseInt("20" + date[1]),
                Integer.parseInt(date[0]) + 1, 1).minusDays(1);

        if (expirationDate.isBefore(LocalDate.now())) throw new BadRequestException("Card is expired!");
    }

    private void checkIfAllParametersAreSame(AcquirerBankPaymentRequest paymentRequest) {
        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getCardDetails().getPan())
                .orElseThrow(() -> new NotFoundException("Something went wrong, try again!"));

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(paymentRequest.getCardDetails().getSecurityCode().toString(), customerBankAcc.getCard().getSecurityCode())) {
            throw new BadRequestException("Something went wrong, try again!");
        }

        if (!customerBankAcc.getCard().getCardHolderName().equals(paymentRequest.getCardDetails().getCardHolderName())) {
            throw new BadRequestException("Something went wrong, try again!");
        }

        if (!customerBankAcc.getCard().getExpireDate().equals(paymentRequest.getCardDetails().getCardExpiresIn())) {
            throw new BadRequestException("Something went wrong, try again!");
        }
    }
}

