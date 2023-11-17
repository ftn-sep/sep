package org.psp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.sep.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Long merchantOrderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime merchantTimeStamp;

    @Column(nullable = false)
    @Enumerated
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private Long paymentId;

    @Column
    private Long acquirerOrderId;
    @Column
    private LocalDateTime acquirerTimestamp;
}
