package org.acquirer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailsPaymentRequest {

    private String uuid;
    @Digits(integer = 10, fraction = 0)
    private Long paymentId;
    @CreditCardNumber(message="Enter valid card number.")
    private String pan;
    @Digits(integer = 3, fraction = 0, message = "Wrong security code.")
    private Integer securityCode;
    @NotNull(message="Enter card holder name.")
    private String cardHolderName;
    @Pattern(regexp = "\\d{2}/\\d{2}")
    private String cardExpiresIn;
}
