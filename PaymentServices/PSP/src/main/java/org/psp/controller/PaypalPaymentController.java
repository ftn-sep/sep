package org.psp.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.psp.model.LogLevel;
import org.psp.service.LoggingService;
import org.psp.service.PaypalPaymentService;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://localhost:4200", "null"}, allowedHeaders = "*")
public class PaypalPaymentController {

    private final PaypalPaymentService paypalPaymentService;
    private final LoggingService loggingService;

    @PostMapping(
            value = "/payment/paypal",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> paypalPayment(@RequestBody PaymentRequestFromClient paymentRequest,
                                           HttpServletRequest httpServletRequest) {

        loggingService.log("Paypal Payment", PaypalPaymentController.class.getName(), httpServletRequest.getRequestURI(),
                LogLevel.DEBUG, paymentRequest.toString(), httpServletRequest.getRemoteAddr());

        PaymentUrlIdResponse paymentUrlIdResponse = paypalPaymentService.paypalPayment(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }
}
