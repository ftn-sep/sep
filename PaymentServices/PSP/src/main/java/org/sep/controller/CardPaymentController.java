package org.sep.controller;

import lombok.RequiredArgsConstructor;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.TransactionDetails;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.service.CardPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CardPaymentController {

    private final CardPaymentService cardPaymentService;

    @PostMapping(
            value = "/card-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardPayment(@RequestBody PaymentRequestFromClient paymentRequest) {
        PaymentUrlIdResponse paymentUrlIdResponse = cardPaymentService.sendRequestForPaymentUrl(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }

    @PostMapping(
            value = "/transaction-details",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updatePaymentDetails(@RequestBody TransactionDetails transactionDetails) {
        cardPaymentService.updatePaymentDetails(transactionDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
