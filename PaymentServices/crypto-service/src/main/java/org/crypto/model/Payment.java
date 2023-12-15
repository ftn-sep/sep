package org.crypto.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String uuid;

    @Column
    private Long merchantOrderId;

    @Column
    private String merchantId;

    @Column
    private LocalDateTime merchantTimestamp;

    @Column
    private double amount;

    @Enumerated
    private PaymentStatus status;

    @Column
    private LocalDateTime validUntil;

    @Column
    private Long coinGateOrderId;

    @Column
    private String successUrl;
    @Column
    private String errorUrl;
    @Column
    private String failedUrl;

    public Payment(UUID uuid, PaymentUrlAndIdRequest paymentRequest, int linkDuration) {
        this.uuid = uuid.toString();
        merchantOrderId = paymentRequest.getMerchantOrderId();
        merchantId = paymentRequest.getMerchantId();
        merchantTimestamp = paymentRequest.getMerchantTimestamp();
        amount = paymentRequest.getAmount();
        successUrl = paymentRequest.getSuccessUrl();
        errorUrl = paymentRequest.getErrorUrl();
        failedUrl = paymentRequest.getFailedUrl();

        status = PaymentStatus.IN_PROGRESS;
        validUntil = LocalDateTime.now().plusMinutes(linkDuration);
    }
}
