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

    private final WebClient.Builder webClientBuilder;
    private final BankAccountRepository bankAccountRepository;
    private final PaymentRepository paymentRepository;

    public void doPayment(Payment payment, CardDetailsPaymentRequest paymentRequest, BankAccount sellerBankAcc) {

        BankAccount customerBankAcc = bankAccountRepository.findByCardPan(paymentRequest.getPan())
                .orElseThrow(() -> new NotFoundException("Customer card doesn't exist in acquirer's bank!"));

        if (customerBankAcc.getBalance() < payment.getAmount()) {
            payment.setStatus(PaymentStatus.FAILED);
            return;
//            throw new BadRequestException("Customer doesn't have enough money");
        }

        customerBankAcc.setBalance(customerBankAcc.getBalance() - payment.getAmount());
        sellerBankAcc.setBalance(sellerBankAcc.getBalance() + payment.getAmount());

        payment.setStatus(PaymentStatus.DONE);

        bankAccountRepository.save(customerBankAcc);
        bankAccountRepository.save(sellerBankAcc);
    }


}
