package org.acquirer.service;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.acquirer.model.BankAccount;
import org.acquirer.model.Payment;
import org.acquirer.repository.BankAccountRepository;
import org.sep.dto.card.CardDetails;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.NotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class SameBankPaymentService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionDetailsService transactionDetailsService;

    public void doPayment(Payment payment, CardDetails paymentRequest, BankAccount sellerBankAcc) {

        String sha512pan = Hashing.sha512()
                .hashString(paymentRequest.getPan(), StandardCharsets.UTF_8)
                .toString();

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(sha512pan)
                .orElseThrow(() -> new NotFoundException("Something went wrong, try again!"));

        validateCard(paymentRequest, customerBankAcc);

        payment.setIssuerAccountNumber(customerBankAcc.getAccountNumber());

        if (customerBankAcc.getBalance() < payment.getAmount()) {
            transactionDetailsService.onFailedPayment(payment);
        } else {
            customerBankAcc.setBalance(customerBankAcc.getBalance() - payment.getAmount());
            sellerBankAcc.setBalance(sellerBankAcc.getBalance() + payment.getAmount());

            bankAccountRepository.save(customerBankAcc);
            bankAccountRepository.save(sellerBankAcc);
        }
    }

    private void validateCard(CardDetails paymentRequest, BankAccount customerBankAcc) {
        checkIfCardParametersAreCorrect(paymentRequest, customerBankAcc);
        validateCardExpirationDate(paymentRequest);
    }

    private void validateCardExpirationDate(CardDetails paymentRequest) {
        String[] date = paymentRequest.getCardExpiresIn().split("/");
        LocalDate expirationDate = LocalDate.of(Integer.parseInt("20" + date[1]),
                Integer.parseInt(date[0]) + 1, 1).minusDays(1);

        if (expirationDate.isBefore(LocalDate.now())) throw new BadRequestException("Card is expired!");
    }

    private void checkIfCardParametersAreCorrect(CardDetails paymentRequest, BankAccount customerBankAcc) {
        BCryptPasswordEncoder bCryptEnc = new BCryptPasswordEncoder();

        if (!bCryptEnc.matches(paymentRequest.getSecurityCode().toString(), customerBankAcc.getCard().getSecurityCode())) {
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
