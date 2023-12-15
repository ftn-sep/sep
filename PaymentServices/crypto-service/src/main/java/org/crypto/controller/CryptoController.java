package org.crypto.controller;


import lombok.RequiredArgsConstructor;
import org.crypto.model.Payment;
import org.crypto.service.PaymentService;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/crypto")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CryptoController {

    private final PaymentService cryptoService;

    @PostMapping(
            value = "/start-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardDetailsPayment(@RequestBody  PaymentUrlAndIdRequest paymentRequest) {

        PaymentUrlIdResponse paymentResult = cryptoService.pay(paymentRequest);

        return new ResponseEntity<>(paymentResult, HttpStatus.OK);

    }

    @GetMapping(
            value = "/get-payments",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getPayments(){
        cryptoService.completePayment();
        return new ResponseEntity<>(new Payment(), HttpStatus.OK);
    }




}
