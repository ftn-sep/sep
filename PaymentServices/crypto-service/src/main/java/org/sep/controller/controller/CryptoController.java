package org.sep.controller.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/crypto")
public class CryptoController {

    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello from Crypto";
    }
}
