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

@Service
@Transactional
@RequiredArgsConstructor
public class IssuerService {

    private final IssuerRepository bankAccountRepository;

    public IssuerBankPaymentResponse cardPayment(AcquirerBankPaymentRequest paymentRequest) {

        IssuerBankPaymentResponse issuerBankPaymentResponse = new IssuerBankPaymentResponse();

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getCardDetails().getPan())
                .orElseThrow(() -> new NotFoundException("Customer card doesn't exist !"));

        if (customerBankAcc.getBalance() < paymentRequest.getAmount()) {
            issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.FAILED);
            throw new BadRequestException("Customer doesn't have enough money");
        }
        customerBankAcc.setBalance(customerBankAcc.getBalance() - paymentRequest.getAmount());
        issuerBankPaymentResponse.setPaymentStatus(PaymentStatus.DONE);

        bankAccountRepository.save(customerBankAcc);

        return issuerBankPaymentResponse;
    }
}
