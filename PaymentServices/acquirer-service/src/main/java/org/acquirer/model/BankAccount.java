package org.acquirer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String pan;

    @Column
    private String cardHolderName;

    @Column
    private String expireDate;

    @Column
    private String securityCode;

    @Column
    private double balance;

    @Column
    private String merchantId;

    @Column
    private String merchantPassword;

}
