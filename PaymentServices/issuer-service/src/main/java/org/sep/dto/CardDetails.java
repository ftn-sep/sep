package org.sep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDetails {

    private String uuid;
    private Long paymentId;
    // todo: validation
    private String pan;
    private Integer securityCode;
    private String cardHolderName;
    private String cardExpiresIn;
}
