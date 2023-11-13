package org.acquirer.controller;

import org.acquirer.service.AcquirerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
