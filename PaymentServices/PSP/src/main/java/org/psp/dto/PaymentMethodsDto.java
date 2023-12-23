package org.psp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sep.enums.PaymentMethod;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodsDto {

    private Set<PaymentMethod> paymentMethods;
    private boolean hasMerchantIdAndPassword;
}
