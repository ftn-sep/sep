package org.crypto.controller.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crypto.dto.CompletePayment;
import org.crypto.dto.OrderDetails;
import org.crypto.dto.PaymentResultResponse;
import org.crypto.service.CryptoService;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/crypto")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @PostMapping(
            value = "/start-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardDetailsPayment(@RequestBody  PaymentUrlAndIdRequest paymentRequest) {

        PaymentUrlIdResponse paymentResult = cryptoService.pay(paymentRequest);

        return new ResponseEntity<>(paymentResult, HttpStatus.OK);

    }

//    @PostMapping(
//            value = "/payment-crypto-details",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> cardDetailsPayment(@Valid @RequestBody OrderDetails orderRequest) {
//
//        PaymentResultResponse paymentResult = cryptoService.pay(orderRequest);
//
//        return new ResponseEntity<>(URI.create(paymentResult.getRedirectUrl()), HttpStatus.OK);
//
//    }

    @PostMapping(
            value = "/complete-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> completePayment(@RequestBody CompletePayment completePaymentRequest) {

        PaymentResultResponse paymentResult = cryptoService.completePayment(completePaymentRequest);

        return new ResponseEntity<>(URI.create(paymentResult.getRedirectUrl()), HttpStatus.OK);

    }




}
