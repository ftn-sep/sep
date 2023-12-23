package org.psp.service;


import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.psp.model.Payment;
import org.psp.repository.PaymentRepository;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.dto.card.TransactionDetails;
import org.sep.enums.PaymentStatus;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient.Builder webClientBuilder;

    public void savePayment(PaymentRequestFromClient paymentRequest, Long paymentId) {
        Payment payment = Payment.builder()
                .merchantOrderId(paymentRequest.getMerchantOrderId())
                .amount(paymentRequest.getAmount())
                .merchantTimeStamp(paymentRequest.getMerchantTimeStamp())
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .paymentId(paymentId)
                .build();

        paymentRepository.save(payment);
    }

    public void savePaypalPayment(PaymentRequestFromClient paymentRequest) {
        Payment payment = Payment.builder()
                .merchantOrderId(paymentRequest.getMerchantOrderId())
                .amount(paymentRequest.getAmount())
                .merchantTimeStamp(paymentRequest.getMerchantTimeStamp())
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build();

        paymentRepository.save(payment);
    }

    public void updatePaymentDetails(TransactionDetails transactionDetails) {

        Payment payment = paymentRepository.findByMerchantOrderId(transactionDetails.getMerchantOrderId())
                .orElseThrow(() -> new NotFoundException("Payment doesn't exist!"));

        payment.setPaymentStatus(transactionDetails.getPaymentStatus());
        payment.setAcquirerTimestamp(transactionDetails.getAcquirerTimestamp());
        payment.setAcquirerOrderId(transactionDetails.getAcquirerOrderId());

        paymentRepository.save(payment);
    }

    public PaymentUrlIdResponse getPaymentUrlAndId(PaymentUrlAndIdRequest paymentReq, String redirectUrl) {
        return webClientBuilder.build().post()
                .uri(redirectUrl)
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
