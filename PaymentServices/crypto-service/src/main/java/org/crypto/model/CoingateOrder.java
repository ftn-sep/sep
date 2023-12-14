package org.crypto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CoingateOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String order_id;

    @Column
    private Double price_amount;

    @Column
    private String price_currency;

    @Column
    private String receive_currency;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String callback_url;

    @Column
    private String cancel_url;

    @Column
    private String success_url;

    @Column
    private String payment_url;

    @Column
    private String token;

    public static CoingateOrderBuilder builder() {
        return new CoingateOrderBuilder()
                .price_currency("USD")
                .receive_currency("DO_NOT_CONVERT");
    }

}
