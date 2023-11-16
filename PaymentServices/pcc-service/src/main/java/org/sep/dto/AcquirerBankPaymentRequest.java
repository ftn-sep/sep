package org.sep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquirerBankPaymentRequest {
    private Long acquirerOrderId;
    private LocalDateTime acquirerTimeStamp;
    private CardDetails cardDetails;
    private double amount;
}

