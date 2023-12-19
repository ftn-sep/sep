package org.psp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedPaymentMethodsDto {
    private String sellerUsername;
    private String accountNumber;
    private List<String> selectedMethods;

}
