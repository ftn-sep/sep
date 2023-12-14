package org.psp.controller;

import lombok.RequiredArgsConstructor;
import org.psp.service.CryptoPaymentService;
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

    @PostMapping(
            value = "/crypto-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cryptoPayment(@RequestBody PaymentRequestFromClient paymentRequest) {
        PaymentUrlIdResponse paymentUrlIdResponse = cryptoPaymentService.cryptoPayment(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }
}
