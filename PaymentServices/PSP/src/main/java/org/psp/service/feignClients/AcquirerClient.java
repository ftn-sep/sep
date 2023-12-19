package org.psp.service.feignClients;

import org.psp.dto.SellersBankInformationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "acquirer-service")
public interface AcquirerClient {

    @GetMapping("/api/acquirer/sellers-info/{accountNumber}")
    ResponseEntity<SellersBankInformationDto> getSellersBankInfo(@PathVariable String accountNumber);
}
