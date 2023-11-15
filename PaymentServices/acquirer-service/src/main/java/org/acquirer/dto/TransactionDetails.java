package org.acquirer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acquirer.model.enums.PaymentStatus;

import java.sql.Timestamp;
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
