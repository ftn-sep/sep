package org.sep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sep.model.enums.PaymentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuerBankPaymentResponse {
    private Long acquirerOrderId;
    private LocalDateTime acquirerTimeStamp;
    private Long issuerOrderId;
    private LocalDateTime issuerTimeStamp;
    private PaymentStatus paymentStatus;
}
