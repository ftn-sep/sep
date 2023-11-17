package org.acquirer.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.acquirer.model.Payment;
import org.acquirer.repository.PaymentRepository;
import org.apache.http.HttpHeaders;
import org.sep.dto.card.IssuerBankPaymentResponse;
import org.sep.dto.card.TransactionDetails;
import org.sep.enums.PaymentStatus;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.ErrorPaymentException;
import org.sep.exceptions.FailedPaymentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionDetailsService {

    private final WebClient.Builder webClientBuilder;
    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onFailedPayment(Payment payment) throws BadRequestException {
        changeStatus(PaymentStatus.FAILED, payment);
        sendTransactionDetailsToPsp(payment, null);
        throw new FailedPaymentException(payment.getFailedUrl());
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onErrorPayment(Payment payment) throws BadRequestException {
        changeStatus(PaymentStatus.ERROR, payment);
        sendTransactionDetailsToPsp(payment, null);
        throw new ErrorPaymentException(payment.getErrorUrl());
    }

    public void onSuccessPayment(Payment payment, IssuerBankPaymentResponse issuerBankResponse) {
        changeStatus(PaymentStatus.DONE, payment);
        sendTransactionDetailsToPsp(payment, issuerBankResponse);
    }

    private void changeStatus(PaymentStatus status, Payment payment) {
        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    private void sendTransactionDetailsToPsp(Payment payment, IssuerBankPaymentResponse issuerBankResponse) {

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .merchantOrderId(payment.getMerchantOrderId())
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus())
                .build();

        if (issuerBankResponse != null) {
            transactionDetails.setAcquirerOrderId(issuerBankResponse.getAcquirerOrderId());
            transactionDetails.setAcquirerTimestamp(issuerBankResponse.getAcquirerTimeStamp());
        }

        webClientBuilder.build().post()
                .uri("http://psp-service/api/psp/transaction-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDetails), TransactionDetails.class)
                .exchange().toFuture();
    }

}
