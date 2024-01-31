package org.sep.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestFromClient {

    @Digits(integer = 8, fraction = 2)
    @DecimalMax(value = "99999999.99")
    @DecimalMin(value = "0.00")
    private Double amount;

    @Digits(integer = 10, fraction = 0)
    private Long merchantOrderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime merchantTimeStamp;

    private String apiKey;

}
