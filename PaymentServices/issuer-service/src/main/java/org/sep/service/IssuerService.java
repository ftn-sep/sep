package org.sep.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.sep.dto.AcquirerBankPaymentRequest;
import org.sep.dto.IssuerBankPaymentResponse;
import org.sep.model.BankAccount;
import org.sep.model.Payment;
import org.sep.model.enums.PaymentStatus;
import org.sep.repository.BankAccountRepository;
import org.sep.repository.PaymentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

        IssuerBankPaymentResponse issuerBankPaymentResponse = new IssuerBankPaymentResponse();

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getCardDetails().getPan())
                .orElseThrow(() -> new NotFoundException("Customer card doesn't exist !"));

        if (customerBankAcc.getBalance() < paymentRequest.getAmount()) {
            issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.FAILED);
            throw new BadRequestException("Customer doesn't have enough money");
        }
        customerBankAcc.setBalance(customerBankAcc.getBalance() - paymentRequest.getAmount());

        issuerBankPaymentResponse.setIssuerOrderId(generateOrderId());
        issuerBankPaymentResponse.setIssuerTimeStamp(LocalDateTime.now());
        issuerBankPaymentResponse.setIssuerAccountNumber(customerBankAcc.getAccountNumber());
        issuerBankPaymentResponse.setAcquirerOrderId(paymentRequest.getAcquirerOrderId());
        issuerBankPaymentResponse.setAcquirerTimeStamp(paymentRequest.getAcquirerTimeStamp());
        issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.DONE);
        bankAccountRepository.save(customerBankAcc);

        Payment payment = Payment.builder()
                .issuerTimestamp(issuerBankPaymentResponse.getIssuerTimeStamp())
                .issuerOrderId(issuerBankPaymentResponse.getIssuerOrderId())
                .status(issuerBankPaymentResponse.getPaymentStatus())
                .amount(paymentRequest.getAmount())
                .acquirerAccountNumber(paymentRequest.getAcquirerAccountNumber())
                .issuerAccountNumber(customerBankAcc.getAccountNumber())
                .acquirerTimestamp(paymentRequest.getAcquirerTimeStamp())
                .acquirerOrderId(paymentRequest.getAcquirerOrderId())
                .build();

        paymentRepository.save(payment);

        return issuerBankPaymentResponse;
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
                .orElseThrow(() -> new NotFoundException("Customer's bank account doesn't exist for given pan"));

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if(!bCryptPasswordEncoder.matches(paymentRequest.getCardDetails().getSecurityCode().toString(), customerBankAcc.getCard().getSecurityCode().toString())){
            throw new BadRequestException("Wrong security code");
        }

        if (!customerBankAcc.getCard().getCardHolderName().equals(paymentRequest.getCardDetails().getCardHolderName())) {
            throw new BadRequestException("Wrong card holder name");
        }

        if (!customerBankAcc.getCard().getExpireDate().equals(paymentRequest.getCardDetails().getCardExpiresIn())) {
            throw new BadRequestException("Expiration date doesn't match");
        }
    }
}

