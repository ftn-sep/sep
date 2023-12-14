package org.crypto.dto;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

    private String uuid;
    @Digits(integer = 10, fraction = 0)
    private Long paymentId;
}
