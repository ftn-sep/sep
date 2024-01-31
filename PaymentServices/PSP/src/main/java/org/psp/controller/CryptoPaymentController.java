package org.psp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.psp.model.LogLevel;
import org.psp.service.CryptoPaymentService;
import org.psp.service.LoggingService;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "null"}, allowedHeaders = "*")
public class CryptoPaymentController {

    private final CryptoPaymentService cryptoPaymentService;
    private final LoggingService loggingService;

    @PostMapping(
            value = "/crypto-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cryptoPayment(@RequestBody PaymentRequestFromClient paymentRequest,
                                           HttpServletRequest httpServletRequest) {

        loggingService.log("Crypto Payment", CryptoPaymentController.class.getName(), httpServletRequest.getRequestURI(),
                LogLevel.DEBUG, paymentRequest.toString(), httpServletRequest.getRemoteAddr());

        PaymentUrlIdResponse paymentUrlIdResponse = cryptoPaymentService.cryptoPayment(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }
}
