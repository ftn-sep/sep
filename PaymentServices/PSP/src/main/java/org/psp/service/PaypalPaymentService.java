package org.psp.service;

import lombok.RequiredArgsConstructor;
import org.psp.model.Seller;
import org.psp.repository.SellerRepository;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.enums.PaymentMethod;
import org.sep.exceptions.NotFoundException;
import org.sep.exceptions.NotSubscribedToPaymentMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaypalPaymentService {

    private final PaymentService paymentService;
    private final SellerRepository sellerRepository;

    private static final String SUCCESS_URL = "http://localhost:4200/success-payment";
    private static final String FAILED_URL = "http://localhost:4200/failed-payment";
    private static final String ERROR_URL = "http://localhost:4200/error-payment";

    public PaymentUrlIdResponse paypalPayment(PaymentRequestFromClient paymentRequest) {
        checkIfSellerIsSubscribedForPaypalPayment(paymentRequest);

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

    private void checkIfSellerIsSubscribedForPaypalPayment(PaymentRequestFromClient paymentRequest) throws NotSubscribedToPaymentMethod {
        Long sellerId = Long.valueOf(paymentRequest.getMerchantOrderId().toString().substring(0, 4));
        Seller seller = sellerRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller doesn't exist!"));

        SubscriberService.checkIfSellerIsSubscribedToMethod(seller, PaymentMethod.PAYPAL);
    }
}
