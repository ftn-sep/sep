package org.acquirer.controller;

import jakarta.validation.Valid;
import org.acquirer.dto.PaymentResultResponse;
import org.acquirer.dto.SellersBankInformationDto;
import org.acquirer.service.AcquirerService;
import org.apache.coyote.Response;
import org.sep.dto.card.CardDetails;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/acquirer")
@CrossOrigin(origins = "http://localhost:4200/")
public class AcquirerController {

    private final AcquirerService acquirerService;

    public AcquirerController(AcquirerService acquirerService) {
        this.acquirerService = acquirerService;
    }

    @PostMapping(
            value = "/payment-url-request",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> paymentUrlAndIdRequest(@RequestBody PaymentUrlAndIdRequest paymentRequest) {
        PaymentUrlIdResponse paymentUrlIdResponse = acquirerService.generatePaymentUrlAndId(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }

    @PostMapping(
            value = "/payment-card-details",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cardDetailsPayment(@Valid @RequestBody CardDetails paymentRequest) {

        PaymentResultResponse paymentResult = acquirerService.cardDetailsPayment(paymentRequest);

        return new ResponseEntity<>(URI.create(paymentResult.getRedirectUrl()), HttpStatus.OK);
    }

    @GetMapping("/sellers-info/{accountNumber}")
    ResponseEntity<SellersBankInformationDto> getSellersBankInfo(@PathVariable String accountNumber) {
        SellersBankInformationDto sellersBankInformationDto = acquirerService.getMerchantInfo(accountNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellersBankInformationDto);
    }
}
