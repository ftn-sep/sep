package org.paypal.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private OrderIntent intent;
    @JsonProperty("purchase_units")
    private List<PurchaseUnitDTO> purchaseUnits;
    @JsonProperty("application_context")
    private PaypalAppContextDTO applicationContext;
}
