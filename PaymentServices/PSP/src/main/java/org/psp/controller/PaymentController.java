package org.psp.controller;

import lombok.RequiredArgsConstructor;
import org.psp.service.PspService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
@RequiredArgsConstructor
public class PaymentController {

    private final PspService pspService;

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
}
