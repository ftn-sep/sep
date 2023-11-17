package org.sep.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquirerToIssuerPaymentRequest {
    private Long acquirerOrderId;
    private LocalDateTime acquirerTimeStamp;
    private CardDetails cardDetails;
    private String acquirerAccountNumber;
    private Double amount;
    private Long paymentId;
}