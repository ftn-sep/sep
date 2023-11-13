package org.sep.controller;

import org.sep.model.dto.PaymentRequest;
import org.sep.service.PspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String payment(@RequestBody PaymentRequest paymentRequest){
        return pspService.savePayment(paymentRequest);
    }


}
