package org.sep.controller;

import org.sep.service.PspService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/psp")
public class PaymentController {

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


}
