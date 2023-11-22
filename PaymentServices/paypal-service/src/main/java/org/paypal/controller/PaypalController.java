package org.paypal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.paypal.service.PaypalService;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/paypal")
public class PaypalController {

    @Autowired
    private PaypalService paypalService;

    @GetMapping(value = "/hello")
    public String hello() {

        return paypalService.hello();
    }

    @PostMapping(
            value = "/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> payment(@RequestBody PaymentUrlAndIdRequest paymentRequest)  throws Exception {
        var PaymentUrlIdResponse = paypalService.createOrder(paymentRequest);
        return ResponseEntity.ok(PaymentUrlIdResponse);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) throws Exception {
        paypalService.confirmOrder(request.getParameter("token"));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:4200/success-payment"))
                .build();
    }
}