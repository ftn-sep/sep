package org.sep.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.sep.dto.AcquirerBankPaymentRequest;
import org.sep.dto.IssuerBankPaymentResponse;
import org.sep.model.BankAccount;
import org.sep.model.enums.PaymentStatus;
import org.sep.repository.IssuerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class IssuerService {

    private final IssuerRepository bankAccountRepository;

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
        issuerBankPaymentResponse.setAcquirerOrderId(paymentRequest.getAcquirerOrderId());
        issuerBankPaymentResponse.setAcquirerTimeStamp(paymentRequest.getAcquirerTimeStamp());
        issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.DONE);

        bankAccountRepository.save(customerBankAcc);

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

        // todo: hesirati securityCode

        if (!customerBankAcc.getCard().getSecurityCode().equals(paymentRequest.getCardDetails().getSecurityCode())) {
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

