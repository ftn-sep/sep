package org.psp.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedPaymentMethodsDto {
    @Email(message = "Seller username is in wrong format")
    private String sellerUsername;

    @Pattern(regexp = "([0-9]{13})?", message = "Account number must have 13 digits only")
    private String accountNumber;

    private List<String> selectedMethods;
}
