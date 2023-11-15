package org.acquirer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acquirer.dto.PaymentUrlAndIdRequest;
import org.acquirer.model.enums.PaymentStatus;

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
    private String uuid; // todo: da li nam treba?

    @Column
    private String merchantId;

    @Column
    private String merchantPassword;

    @Column
    private Long merchantOrderId;

    @Column
    private double amount;

    @Column
    private LocalDateTime merchantTimestamp;

    @Enumerated
    private PaymentStatus status;

    @Column
    private LocalDateTime validUntil;

    @Column
    private String successUrl;
    @Column
    private String errorUrl;
    @Column
    private String failedUrl;

    public Payment(UUID uuid, PaymentUrlAndIdRequest paymentRequest, int linkDuration) {
        this.uuid = uuid.toString();
        merchantId = paymentRequest.getMerchantId();
        merchantPassword = paymentRequest.getMerchantPassword();
        merchantOrderId = paymentRequest.getMerchantOrderId();
        merchantTimestamp = paymentRequest.getMerchantTimestamp();
        amount = paymentRequest.getAmount();
        successUrl = paymentRequest.getSuccessUrl();
        errorUrl = paymentRequest.getErrorUrl();
        failedUrl = paymentRequest.getFailedUrl();

        status = PaymentStatus.IN_PROGRESS;
        validUntil = LocalDateTime.now().plusMinutes(linkDuration);

    }
}
