package org.sep.dto.card;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDetails {

    private String uuid;
    @Digits(integer = 10, fraction = 0)
    private Long paymentId;
    //@CreditCardNumber(message = "Enter valid card number.")
    private String pan;
    @Digits(integer = 3, fraction = 0, message = "Wrong security code.")
    private Integer securityCode;
    @NotEmpty(message = "Enter card holder name.")
    private String cardHolderName;
    @Pattern(regexp = "\\d{2}/\\d{2}")
    private String cardExpiresIn;
}
