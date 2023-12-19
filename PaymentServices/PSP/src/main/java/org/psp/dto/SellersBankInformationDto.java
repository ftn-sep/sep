package org.psp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellersBankInformationDto {

    private String merchantId;
    private String merchantPassword;

}
