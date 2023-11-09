package org.sep.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/acquirer")
public class AcquirerController {

    @GetMapping(value = "/hello")
    public String hello(@RequestParam String text) {
        return "Hello from Acquirer";
    }
}
