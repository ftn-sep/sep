package org.sep.controller;

import org.sep.dto.AcquirerBankPaymentRequest;
import org.sep.dto.IssuerBankPaymentResponse;
import org.sep.service.PccService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/pcc")
public class PccController {

    private final PccService pccService;

    public PccController(PccService pccService) {
        this.pccService = pccService;
    }

    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello from PCC";
    }

    @GetMapping(value = "/card-payment")
    public String cardPayment() {
        System.out.println("Hello from PCC !");
        return this.pccService.pingIssuer();
    }

    @PostMapping(
            value = "/payment-card-details",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardDetailsPayment(@RequestBody AcquirerBankPaymentRequest paymentRequest) {
        IssuerBankPaymentResponse issuerBankPaymentResponse = pccService.cardPayment(paymentRequest);
        return new ResponseEntity<>(issuerBankPaymentResponse, HttpStatus.OK);
    }
}
