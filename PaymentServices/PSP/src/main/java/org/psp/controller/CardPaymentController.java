package org.psp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psp.model.LogLevel;
import org.psp.model.Payment;
import org.psp.service.CardPaymentService;
import org.psp.service.LoggingService;
import org.psp.service.PaymentService;
import org.sep.dto.PaymentRequestFromClient;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.dto.card.TransactionDetails;
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
    private final PaymentService paymentService;
    private final LoggingService loggingService;

    @PostMapping(
            value = "/payment/card",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardPayment(@Valid @RequestBody PaymentRequestFromClient paymentRequest,
                                         HttpServletRequest httpServletRequest) {

        loggingService.log("Card Payment Initialization", CardPaymentController.class.getName(), httpServletRequest.getRequestURI(),
                LogLevel.DEBUG, paymentRequest.toString(), httpServletRequest.getRemoteAddr());

        PaymentUrlIdResponse paymentUrlIdResponse = cardPaymentService.sendRequestForPaymentUrl(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }

    @PostMapping(
            value = "/transaction-details",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updatePaymentDetails(@RequestBody TransactionDetails transactionDetails,
                                                  HttpServletRequest httpServletRequest) {

        loggingService.log("Updating Transaction Details", CardPaymentController.class.getName(),
                httpServletRequest.getRequestURI(), LogLevel.DEBUG, transactionDetails.toString(), httpServletRequest.getRemoteAddr());

        paymentService.updatePaymentDetails(transactionDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
