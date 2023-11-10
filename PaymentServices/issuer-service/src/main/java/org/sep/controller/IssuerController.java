package org.sep.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/issuer")
public class IssuerController {

    @GetMapping(value = "/card-payment")
    public String hello() {
        System.out.println("Hello from Issuer !");
        return "Card payment successfully !";
    }
}
