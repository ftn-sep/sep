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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column
    private double balance;

    @Column
    private String accountNumber;

    @Column
    private String merchantId;

    @Column
    private String merchantPassword;

}
