package org.psp.controller;

        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.validation.Valid;
        import lombok.RequiredArgsConstructor;
        import org.psp.model.LogLevel;
        import org.psp.service.LoggingService;
        import org.psp.service.QrCodePaymentService;
        import org.sep.dto.PaymentRequestFromClient;
        import org.sep.dto.card.PaymentUrlIdResponse;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/psp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QrCodePaymentController {

    private final QrCodePaymentService qrCodePaymentService;
    private final LoggingService loggingService;

    @PostMapping(
            value = "/payment/qrcode",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> qrcodePayment(@Valid @RequestBody PaymentRequestFromClient paymentRequest,
                                           HttpServletRequest httpServletRequest) {

        loggingService.log("QR Payment", QrCodePaymentController.class.getName(), httpServletRequest.getRequestURI(),
                LogLevel.DEBUG, paymentRequest.toString(), httpServletRequest.getRemoteAddr());

        PaymentUrlIdResponse paymentUrlIdResponse = qrCodePaymentService.generateQRCode(paymentRequest);
        return new ResponseEntity<>(paymentUrlIdResponse, HttpStatus.OK);
    }
}
