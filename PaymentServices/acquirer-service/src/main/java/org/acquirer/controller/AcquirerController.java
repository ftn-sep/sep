package org.acquirer.controller;

import org.acquirer.dto.CardDetailsPaymentRequest;
import org.acquirer.dto.PaymentResultResponse;
import org.acquirer.dto.PaymentUrlAndIdRequest;
import org.acquirer.dto.PaymentUrlIdResponse;
import org.acquirer.service.AcquirerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/acquirer")
public class AcquirerController {

    private final AcquirerService acquirerService;

    public AcquirerController(AcquirerService acquirerService) {
        this.acquirerService = acquirerService;
    }

    @GetMapping(value = "/hello")
    public String hello(@RequestParam String text) {
        return "Hello from Acquirer";
    }

    @GetMapping(value = "/card-payment")
    public String cardPayment() {
        System.out.println("Hello from Acquirer !");
        return this.acquirerService.pingPcc();
    }

    @PostMapping(
            value = "/payment-url-request",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> paymentUrlAndIdRequest(@RequestBody PaymentUrlAndIdRequest paymentRequest) {

        PaymentUrlIdResponse paymentUrlIdResponse = acquirerService.generatePaymentUrlAndId(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }

    @PostMapping(
            value = "/payment-card-details",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardDetailsPayment(@RequestBody CardDetailsPaymentRequest paymentRequest) {

        // todo: ovde bi trebalo async ili ne

        PaymentResultResponse paymentResult = acquirerService.cardDetailsPayment(paymentRequest);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(paymentResult.getRedirectUrl()))
                .build();
    }
}
