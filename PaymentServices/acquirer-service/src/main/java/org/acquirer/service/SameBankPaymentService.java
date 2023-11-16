package org.acquirer.service;

import lombok.RequiredArgsConstructor;
import org.acquirer.dto.CardDetailsPaymentRequest;
import org.acquirer.exception.BadRequestException;
import org.acquirer.exception.NotFoundException;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.model.enums.PaymentStatus;
import org.acquirer.repository.BankAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class SameBankPaymentService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionDetailsService transactionDetailsService;

    public void doPayment(Payment payment, CardDetailsPaymentRequest paymentRequest, BankAccount sellerBankAcc) {

        validateCard(paymentRequest);

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getPan())
                .orElseThrow(() -> new NotFoundException("Customer card doesn't exist in acquirer's bank!"));

        payment.setIssuerAccountNumber(customerBankAcc.getAccountNumber());

        if (customerBankAcc.getBalance() < payment.getAmount()) {
            transactionDetailsService.onFailedPayment(PaymentStatus.FAILED, payment, "You don't have enough money");
        } else {
            customerBankAcc.setBalance(customerBankAcc.getBalance() - payment.getAmount());
            sellerBankAcc.setBalance(sellerBankAcc.getBalance() + payment.getAmount());

            bankAccountRepository.save(customerBankAcc);
            bankAccountRepository.save(sellerBankAcc);
        }
    }

    private void validateCard(CardDetailsPaymentRequest paymentRequest) {
        checkIfCardParametersAreCorrect(paymentRequest);
        validateCardExpirationDate(paymentRequest);
    }

    private void validateCardExpirationDate(CardDetailsPaymentRequest paymentRequest) {
        String[] date = paymentRequest.getCardExpiresIn().split("/");
        LocalDate expirationDate = LocalDate.of(Integer.parseInt("20" + date[1]),
                Integer.parseInt(date[0]) + 1, 1).minusDays(1);

        if (expirationDate.isBefore(LocalDate.now())) throw new BadRequestException("Card is expired!");
    }

    private void checkIfCardParametersAreCorrect(CardDetailsPaymentRequest paymentRequest) {
        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getPan())
                .orElseThrow(() -> new NotFoundException("Something went wrong, try again!"));

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(paymentRequest.getSecurityCode().toString(), customerBankAcc.getCard().getSecurityCode())) {
            throw new BadRequestException("Something went wrong, try again!");
        }

        if (!customerBankAcc.getCard().getCardHolderName().equals(paymentRequest.getCardHolderName())) {
            throw new BadRequestException("Something went wrong, try again!");
        }

        if (!customerBankAcc.getCard().getExpireDate().equals(paymentRequest.getCardExpiresIn())) {
            throw new BadRequestException("Something went wrong, try again!");
        }
    }


}
