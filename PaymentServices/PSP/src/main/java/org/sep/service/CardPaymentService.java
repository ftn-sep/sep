package org.sep.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.TransactionDetails;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.exception.BadRequestException;
import org.sep.exception.NotFoundException;
import org.sep.model.Payment;
import org.sep.model.Seller;
import org.sep.model.enums.PaymentStatus;
import org.sep.repository.PaymentRepository;
import org.sep.repository.SellerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class CardPaymentService {


    private final WebClient.Builder webClientBuilder;
    private final PaymentRepository paymentRepository;
    private final SellerRepository sellerRepository;

    private static final String SUCCESS_URL = "http://localhost:4200/success-payment";
    private static final String FAILED_URL = "http://localhost:4200/failed-payment";
    private static final String ERROR_URL = "http://localhost:4200/error-payment";


    private void savePayment(PaymentRequestFromClient paymentRequest, Long paymentId) {
        Payment payment = Payment.builder()
                .merchantOrderId(paymentRequest.getMerchantOrderId())
                .amount(paymentRequest.getAmount())
                .merchantTimeStamp(paymentRequest.getMerchantTimeStamp())
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .paymentId(paymentId)
                .build();

        paymentRepository.save(payment);
    }

    public PaymentUrlIdResponse sendRequestForPaymentUrl(PaymentRequestFromClient paymentRequest) {

        Long sellerId = Long.valueOf(paymentRequest.getMerchantOrderId().toString().substring(0, 4));

        Seller seller = sellerRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller doesn't exist!"));

        PaymentUrlAndIdRequest paymentReq = PaymentUrlAndIdRequest.builder()
                .merchantId(seller.getMerchantId())
                .merchantPassword(seller.getMerchantPassword())
                .amount(paymentRequest.getAmount())
                .merchantOrderId(paymentRequest.getMerchantOrderId())
                .merchantTimestamp(paymentRequest.getMerchantTimeStamp())
                .successUrl(SUCCESS_URL)
                .errorUrl(ERROR_URL)
                .failedUrl(FAILED_URL)
                .build();

        PaymentUrlIdResponse paymentUrlAndId = getPaymentUrlAndId(paymentReq);
        savePayment(paymentRequest, paymentUrlAndId.getPaymentId());

        return paymentUrlAndId;
    }

    private PaymentUrlIdResponse getPaymentUrlAndId(PaymentUrlAndIdRequest paymentReq) {
        return webClientBuilder.build().post()
                .uri("http://acquirer-service/api/acquirer/payment-url-request")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentReq), PaymentUrlAndIdRequest.class)
                .retrieve()

                .onStatus(HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(BadRequestException::new))

                .bodyToMono(PaymentUrlIdResponse.class)
                .block();
    }

    public void updatePaymentDetails(TransactionDetails transactionDetails) {

        Payment payment = paymentRepository.findByPaymentId(transactionDetails.getPaymentId())
                .orElseThrow(() -> new NotFoundException("Payment doesn't exist!"));

        payment.setPaymentStatus(transactionDetails.getPaymentStatus());
        payment.setAcquirerTimestamp(transactionDetails.getAcquirerTimestamp());
        payment.setAcquirerOrderId(transactionDetails.getAcquirerOrderId());

        paymentRepository.save(payment);
    }
}
