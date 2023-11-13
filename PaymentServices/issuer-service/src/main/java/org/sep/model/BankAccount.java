package org.sep.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private Long pan;

    @Column
    private String cardHolderName;

    @Column
    private String expireDate;

    @Column
    private String securityCode;

    @Column
    private double balance;
}

