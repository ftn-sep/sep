package org.acquirer.dto.qrcode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeGenerateResponseDTO {
    private StatusQrCodeDTO s;
    private String t;
    private RequestDTO n;
    private String i;
}
