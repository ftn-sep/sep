package org.psp.controller;

import lombok.RequiredArgsConstructor;
import org.psp.service.PspService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
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

    @GetMapping(value = "/ping-crypto",
            produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> pingCrypto() {
        return new ResponseEntity<>("Hello from PSP \n" + this.pspService.pingCrypto(), HttpStatus.OK);
    }

    @GetMapping(value = "/ping-card-payment")
    public String pingCardPayment() {
        System.out.println("Hello from PSP !");
        return this.pspService.pingCardPayment();
    }
}
