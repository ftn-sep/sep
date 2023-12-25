package org.crypto.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompletePayment {
    private Long id;
    private Long order_id;
    private String status;
    private Double price_amount;
    private String price_currency;
    private String receive_currency;
    private Double receive_amount;
    private Double pay_amount;
    private String pay_currency;
    private String created_at;
}
