package org.paypal.dto.payment;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class PurchaseUnitDTO {
    private MoneyDTO amount;
}
