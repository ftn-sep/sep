package org.sep.controller;

import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.TransactionDetails;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.service.PspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
public class PaymentController {

    @Autowired
    private final PspService pspService;

    public PaymentController(PspService pspService) {
        this.pspService = pspService;
    }

    @GetMapping
    public String hello() {
        return "hello from psp";
    }

    @GetMapping(value = "/ping-acq")
    public String pingAcq() {
        return this.pspService.pingAcquirerService("testtstlalal");
    }

    @GetMapping(value = "/ping-paypal")
    public String pingPaypal() {
        return this.pspService.pingPaypal();
    }

    @GetMapping(value = "/ping-crypto")
    public String pingCrypto() {
        return this.pspService.pingCrypto();
    }

    @GetMapping(value = "/ping-card-payment")
    public String pingCardPayment() {
        System.out.println("Hello from PSP !");
        return this.pspService.pingCardPayment();
    }

    @PostMapping(
            value = "/card-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardPayment(@RequestBody PaymentRequestFromClient paymentRequest) {

        PaymentUrlIdResponse paymentUrlIdResponse = pspService.sendRequestForPaymentUrl(paymentRequest);
        // todo: redirekcija odmah ovde ili na frontu
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }

    @PostMapping(
            value = "/transaction-details",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updatePaymentDetails(@RequestBody TransactionDetails transactionDetails) {
        pspService.updatePaymentDetails(transactionDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
