package org.psp.service;

import lombok.RequiredArgsConstructor;
import org.psp.repository.PaymentRepository;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
public class PaypalPaymentService {

    private final PaymentService paymentService;

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

        PaymentUrlIdResponse paymentUrlAndId = paymentService.getPaymentUrlAndId(paymentReq, "http://paypal-service/api/paypal/payment");
        paymentService.savePaypalPayment(paymentRequest);
        return paymentUrlAndId;
    }
}
