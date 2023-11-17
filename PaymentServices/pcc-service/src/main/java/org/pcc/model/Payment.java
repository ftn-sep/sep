package org.pcc.model;

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
    private Long acquirerOrderId;

    @Column
    private LocalDateTime acquirerTimeStamp;

    @Column
    private Long issuerOrderId;

    @Column
    private LocalDateTime issuerTimeStamp;

    @Column
    private double amount;

    @Enumerated
    private PaymentStatus status;

}

