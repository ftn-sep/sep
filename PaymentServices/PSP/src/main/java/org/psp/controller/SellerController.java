package org.psp.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.psp.dto.PaymentMethodsDto;
import org.psp.dto.SelectedPaymentMethodsDto;
import org.psp.dto.SellerDto;
import org.psp.service.SellerService;
import org.sep.enums.PaymentMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/psp")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/payment-methods")
    public ResponseEntity<SellerDto> newPaymentMethods(@RequestBody SelectedPaymentMethodsDto selectedPaymentMethodsDto) {

        SellerDto sellerDto = sellerService.newPaymentMethods(selectedPaymentMethodsDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellerDto);
    }

    @GetMapping("/subscribed-methods")
    public ResponseEntity<PaymentMethodsDto> getSubscribedPaymentMethods(@RequestParam(required = false) String merchantOrderId,
                                                                         @RequestParam(required = false) String username) {
        PaymentMethodsDto paymentMethodsDto = sellerService.getSubscribedPaymentMethods(merchantOrderId, username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentMethodsDto);
    }
}
