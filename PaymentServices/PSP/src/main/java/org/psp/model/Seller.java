package org.psp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sep.enums.PaymentMethod;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String username;

    @Column
    private Long sellerId; // 1000-9999

    @Column
    private String name;

    @Column
    private String merchantId;

    @Column
    private String merchantPassword;

    @ElementCollection(targetClass = PaymentMethod.class)
    @Enumerated(EnumType.STRING)
    private Set<PaymentMethod> availablePaymentMethods = new HashSet<>();


}