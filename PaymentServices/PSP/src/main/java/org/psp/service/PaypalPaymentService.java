package org.psp.service;

import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.exceptions.BadRequestException;
import org.sep.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class PaypalPaymentService {

    private final WebClient.Builder webClientBuilder;
    private static final String SUCCESS_URL = "http://localhost:4200/success-payment";
    private static final String FAILED_URL = "http://localhost:4200/failed-payment";
    private static final String ERROR_URL = "http://localhost:4200/error-payment";

    public PaymentUrlIdResponse paypalPayment(PaymentRequestFromClient paymentRequest) {

        PaymentUrlAndIdRequest paymentReq = PaymentUrlAndIdRequest.builder()
                .amount(paymentRequest.getAmount())
                .merchantOrderId(paymentRequest.getMerchantOrderId())
                .merchantTimestamp(paymentRequest.getMerchantTimeStamp())
                .successUrl(SUCCESS_URL)
                .errorUrl(ERROR_URL)
                .failedUrl(FAILED_URL)
                .build();

        return  getPaymentUrlAndId(paymentReq);
    }

    private PaymentUrlIdResponse getPaymentUrlAndId(PaymentUrlAndIdRequest paymentReq) {
        return webClientBuilder.build().post()
                .uri("http://paypal-service/api/paypal/payment")
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
}
