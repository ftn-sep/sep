package org.paypal.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
public class MoneyDTO {
    @JsonProperty("currency_code")
    private String currencyCode;
    private String value;
}