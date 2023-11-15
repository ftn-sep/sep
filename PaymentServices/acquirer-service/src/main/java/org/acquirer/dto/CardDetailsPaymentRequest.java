package org.acquirer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailsPaymentRequest {

    private String uuid;
    private Long paymentId;
    // todo: validation
    private String pan;
    private int securityCode;
    private String cardHolderName;
    private String cardExpiresIn;
}
