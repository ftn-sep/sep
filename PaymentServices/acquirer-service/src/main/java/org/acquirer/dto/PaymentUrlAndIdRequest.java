package org.acquirer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUrlAndIdRequest {

    private String merchantId;
    private String merchantPassword;
    private double amount;
    private Long merchantOrderId;
    private LocalDateTime merchantTimestamp;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
}