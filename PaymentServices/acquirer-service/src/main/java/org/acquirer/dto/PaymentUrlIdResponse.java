package org.acquirer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlIdResponse {

    private String paymentUrl;
    private Long paymentId;
    private double amount;
}
