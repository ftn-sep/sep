package org.crypto.service;

import lombok.RequiredArgsConstructor;
import org.crypto.dto.CompletePayment;
import org.crypto.dto.PaymentResultResponse;
import org.crypto.model.CoingateOrder;
import org.crypto.model.Payment;
import org.crypto.model.Wallet;
import org.crypto.repository.PaymentRepository;
import org.crypto.repository.WalletRepository;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.enums.PaymentStatus;
import org.sep.exceptions.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CryptoService {

    private final WebClient.Builder webClientBuilder;
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;

    private static final int PAYMENT_LINK_DURATION_MINUTES = 15;
    private static final String COINGATE_URL = "https://api-sandbox.coingate.com/v2/orders";

    public PaymentUrlIdResponse pay(PaymentUrlAndIdRequest paymentRequest) {

        // todo validation
        Payment payment = new Payment(UUID.randomUUID(), paymentRequest, PAYMENT_LINK_DURATION_MINUTES);

        Wallet sellerWallet = walletRepository.findByMerchantId(payment.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Seller's bank account doesn't exist in acquire bank"));

        CoingateOrder order = CoingateOrder.builder()
                .order_id(payment.getMerchantOrderId().toString())
                .price_amount(payment.getAmount())
                .cancel_url(payment.getFailedUrl())
                .success_url(payment.getSuccessUrl())
                .callback_url("http://localhost:8083/api/crypto/complete-payment")
                .token(payment.getUuid())
                .build();

        CoingateOrder coinGateResponse = webClientBuilder.build().post()
                .uri(COINGATE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sellerWallet.getCoingateApiKey())
                .body(Mono.just(order), CoingateOrder.class)
                .retrieve()
                .bodyToMono(CoingateOrder.class)
                .block();


        payment.setCoinGateOrderId(coinGateResponse.getOrder_id());
        paymentRepository.save(payment);

        return new PaymentUrlIdResponse(coinGateResponse.getPayment_url(), payment.getId(), payment.getAmount());
    }

    public PaymentResultResponse completePayment(CompletePayment completePaymentRequest) {

        Payment payment = paymentRepository.findPaymentByCoinGateOrderId(completePaymentRequest.getOrder_id().toString())
                .orElseThrow(() -> new NotFoundException("Payment doesn't exist !"));

        PaymentResultResponse paymentResultResponse = getPaymentResultResponse(completePaymentRequest, payment);

        paymentRepository.save(payment);

        return paymentResultResponse;

    }

    private static PaymentResultResponse getPaymentResultResponse(CompletePayment completePaymentRequest, Payment payment) {
        PaymentResultResponse paymentResultResponse = new PaymentResultResponse();

        if(Objects.equals(completePaymentRequest.getStatus(), "paid")){
            payment.setStatus(PaymentStatus.DONE);
            paymentResultResponse.setRedirectUrl(payment.getSuccessUrl());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentResultResponse.setRedirectUrl(payment.getFailedUrl());
        }

        return paymentResultResponse;
    }
}
