package org.paypal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaypalPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String paypalOrderId;
    private String paypalOrderStatus;

    @Column
    private LocalDateTime issuerTimeStamp;

    @Column
    private double amount;

    @Column
    private Long merchantOrderId;

}