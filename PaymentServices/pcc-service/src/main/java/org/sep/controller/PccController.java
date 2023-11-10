package org.sep.controller;

import org.sep.service.PccService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
