package org.acquirer.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.acquirer.dto.CardDetailsPaymentRequest;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.model.enums.PaymentStatus;
import org.acquirer.repository.BankAccountRepository;
import org.acquirer.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class SameBankPaymentService {

    private final BankAccountRepository bankAccountRepository;

    public void doPayment(Payment payment, CardDetailsPaymentRequest paymentRequest, BankAccount sellerBankAcc) {

        validateCard(paymentRequest, payment);

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getPan())
                .orElseThrow(() -> new NotFoundException("Customer card doesn't exist in acquirer's bank!"));

        if (customerBankAcc.getBalance() < payment.getAmount()) {
            payment.setStatus(PaymentStatus.FAILED);
            return;
//            throw new BadRequestException("Customer doesn't have enough money");
        }

        customerBankAcc.setBalance(customerBankAcc.getBalance() - payment.getAmount());
        sellerBankAcc.setBalance(sellerBankAcc.getBalance() + payment.getAmount());

        bankAccountRepository.save(customerBankAcc);
        bankAccountRepository.save(sellerBankAcc);
    }

    private void validateCard(CardDetailsPaymentRequest paymentRequest, Payment payment) {
        checkIfAllParametersAreSame(paymentRequest, payment);
        validateCardExpirationDate(paymentRequest);
    }

    private void validateCardExpirationDate(CardDetailsPaymentRequest paymentRequest) {
        String[] date = paymentRequest.getCardExpiresIn().split("/");
        LocalDate expirationDate = LocalDate.of(Integer.parseInt("20" + date[1]),
                Integer.parseInt(date[0]) + 1, 1).minusDays(1);

        if (expirationDate.isBefore(LocalDate.now())) throw new BadRequestException("Card is expired!");
    }

    private void checkIfAllParametersAreSame(CardDetailsPaymentRequest paymentRequest, Payment payment) {
        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getPan())
                .orElseThrow(() -> new NotFoundException("Customer's bank account doesn't exist for given pan"));

        // todo: hesirati securityCode

        if (!customerBankAcc.getCard().getSecurityCode().equals(paymentRequest.getSecurityCode())) {
            throw new BadRequestException("Wrong security code");
        }

        if (!customerBankAcc.getCard().getCardHolderName().equals(paymentRequest.getCardHolderName())) {
            throw new BadRequestException("Wrong card holder name");
        }

        if (!customerBankAcc.getCard().getExpireDate().equals(paymentRequest.getCardExpiresIn())) {
            throw new BadRequestException("Expiration date doesn't match");
        }
    }


}
