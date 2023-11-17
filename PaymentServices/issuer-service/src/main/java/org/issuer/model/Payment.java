package org.issuer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sep.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Payment {

    @Id
    private Long id;

    @Column
    private Long issuerOrderId;

    @Column
    private LocalDateTime issuerTimestamp;

    @Column
    private Long acquirerOrderId;

    @Column
    private LocalDateTime acquirerTimestamp;

    @Column
    private double amount;

    @Enumerated
    private PaymentStatus status;

    @Column
    private String acquirerAccountNumber;

    @Column
    private String issuerAccountNumber;

}
