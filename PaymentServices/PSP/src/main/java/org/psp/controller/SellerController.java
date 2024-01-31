package org.psp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.psp.dto.PaymentMethodsDto;
import org.psp.dto.SelectedPaymentMethodsDto;
import org.psp.dto.SellerDto;
import org.psp.model.LogLevel;
import org.psp.service.LoggingService;
import org.psp.service.SellerService;
import org.sep.enums.PaymentMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/psp")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class SellerController {

    private final SellerService sellerService;
    private final LoggingService loggingService;

    @PostMapping("/payment-methods")
    public ResponseEntity<SellerDto> newPaymentMethods(@Valid @RequestBody SelectedPaymentMethodsDto selectedPaymentMethodsDto,
                                                       HttpServletRequest httpServletRequest) {

        loggingService.log("New Payment Method", SellerController.class.getName(), httpServletRequest.getRequestURI(),
                LogLevel.DEBUG, selectedPaymentMethodsDto.toString(), httpServletRequest.getRemoteAddr());

        SellerDto sellerDto = sellerService.newPaymentMethods(selectedPaymentMethodsDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellerDto);
    }

    @GetMapping("/subscribed-methods")
    public ResponseEntity<PaymentMethodsDto> getSubscribedPaymentMethods(@RequestParam(required = false) String merchantOrderId,
                                                                         @Valid @RequestParam(required = false) @Email String username,
                                                                         HttpServletRequest httpServletRequest) {

        loggingService.log("Check Subscribed Payment Methods", SellerController.class.getName(), httpServletRequest.getRequestURI(),
                LogLevel.DEBUG, "", httpServletRequest.getRemoteAddr());

        PaymentMethodsDto paymentMethodsDto = sellerService.getSubscribedPaymentMethods(merchantOrderId, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentMethodsDto);
    }
}
