package org.crypto.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.crypto.dto.OrderResponse;
import org.crypto.dto.CoingateOrder;
import org.crypto.model.Payment;
import org.crypto.model.Wallet;
import org.crypto.repository.PaymentRepository;
import org.crypto.repository.WalletRepository;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.dto.card.TransactionDetails;
import org.sep.enums.PaymentStatus;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final WebClient.Builder webClientBuilder;
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;

    private static final int PAYMENT_LINK_DURATION_MINUTES = 15;
    private static final String COINGATE_URL = "https://api-sandbox.coingate.com/v2/orders";

    public PaymentUrlIdResponse pay(PaymentUrlAndIdRequest paymentRequest) {

        validateRequest(paymentRequest);

        Payment payment = new Payment(UUID.randomUUID(), paymentRequest, PAYMENT_LINK_DURATION_MINUTES);

        Wallet sellerWallet = walletRepository.findByMerchantId(payment.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Seller's bank account doesn't exist in acquire bank"));

        CoingateOrder order = CoingateOrder.builder()
                .order_id(payment.getMerchantOrderId().toString())
                .price_amount(payment.getAmount())
                .cancel_url(payment.getFailedUrl())
                .success_url(payment.getSuccessUrl())
                .callback_url("http://localhost:8083/api/crypto/complete-payment")
                .callback_url(payment.getSuccessUrl())
                .token(payment.getUuid())
                .build();

        CoingateOrder coinGateResponse = webClientBuilder.build().post()
                .uri(COINGATE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sellerWallet.getCoingateApiKey())
                .body(Mono.just(order), CoingateOrder.class)
                .retrieve()
                .bodyToMono(CoingateOrder.class)
                .block();


        payment.setCoinGateOrderId(coinGateResponse.getId());
        paymentRepository.save(payment);

        return new PaymentUrlIdResponse(coinGateResponse.getPayment_url(), payment.getId(), payment.getAmount());
    }

    @Scheduled(fixedDelay = 60000)
    public void completePayment() {

        List<Payment> unfinishedPayments = paymentRepository.findAllByStatus(PaymentStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("There are no payments that are in progress !"));

        for (Payment unfinishedPayment: unfinishedPayments) {

            OrderResponse coinGateResponse = webClientBuilder.build().get()
                    .uri(COINGATE_URL + "/" + unfinishedPayment.getCoinGateOrderId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + "1Yruy3kfXeReR8VjnFrVPEL2RbjFVYYZ2bz5Ziz4")
                    .retrieve()
                    .bodyToMono(OrderResponse.class)
                    .block();

            changePaymentStatus(unfinishedPayment, coinGateResponse);
            sendTransactionDetailsToPsp(unfinishedPayment);
        }
    }

    private void changePaymentStatus(Payment unfinishedPayment, OrderResponse coinGateResponse) {
        if(coinGateResponse.getStatus().equals("paid")) {
            unfinishedPayment.setStatus(PaymentStatus.DONE);
            paymentRepository.save(unfinishedPayment);
        } else {
            unfinishedPayment.setStatus(PaymentStatus.ERROR);
            paymentRepository.save(unfinishedPayment);
        }
    }

    private void sendTransactionDetailsToPsp(Payment payment) {
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .merchantOrderId(payment.getMerchantOrderId())
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus())
                .build();

        webClientBuilder.build().post()
                .uri("http://localhost:8080/api/psp/transaction-details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDetails), TransactionDetails.class)
                .exchange().toFuture();
    }

    private void validateRequest(PaymentUrlAndIdRequest paymentRequest) {
        Payment existingPayment = paymentRepository.findPaymentByMerchantOrderId(paymentRequest.getMerchantOrderId())
                .orElse(null);

        if (existingPayment != null) throw new BadRequestException("Payment doesn't exist !");
    }
}
