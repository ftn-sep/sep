package org.sep.controller;

import lombok.RequiredArgsConstructor;
import org.sep.dto.AcquirerBankPaymentRequest;
import org.sep.dto.IssuerBankPaymentResponse;
import org.sep.service.IssuerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/issuer")
@RequiredArgsConstructor
public class IssuerController {

    private final IssuerService issuerService;

    @GetMapping(value = "/card-payment")
    public String hello() {
        System.out.println("Hello from Issuer !");
        return "Card payment successfully !";
    }

    @PostMapping(
            value = "/payment-card-details",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardDetailsPayment(@RequestBody AcquirerBankPaymentRequest paymentRequest) {
        IssuerBankPaymentResponse issuerBankPaymentResponse = issuerService.cardPayment(paymentRequest);
        return new ResponseEntity<>(issuerBankPaymentResponse, HttpStatus.OK);
    }
}
