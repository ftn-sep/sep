package org.sep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sep.model.enums.PaymentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetails {

    private PaymentStatus paymentStatus;
    private Long acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private Long paymentId;
    private Long merchantOrderId;

}