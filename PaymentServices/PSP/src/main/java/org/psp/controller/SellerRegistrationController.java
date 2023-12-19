package org.psp.controller;

import lombok.RequiredArgsConstructor;
import org.psp.dto.SelectedPaymentMethodsDto;
import org.psp.dto.SellerDto;
import org.psp.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/psp")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SellerRegistrationController {

    private final SellerService sellerService;

    @PostMapping("/payment-methods")
    public ResponseEntity<SellerDto> newPaymentMethods(@RequestBody SelectedPaymentMethodsDto selectedPaymentMethodsDto) {

        SellerDto sellerDto = sellerService.newPaymentMethods(selectedPaymentMethodsDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellerDto);
    }
}
