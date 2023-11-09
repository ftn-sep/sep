package org.sep.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/paypal")
public class PaypalController {

    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello from Paypal";
    }
}
