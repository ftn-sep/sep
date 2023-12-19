package org.psp.service;

import lombok.RequiredArgsConstructor;
import org.psp.dto.SelectedPaymentMethodsDto;
import org.psp.dto.SellerDto;
import org.psp.dto.SellersBankInformationDto;
import org.psp.model.Seller;
import org.psp.repository.SellerRepository;
import org.psp.service.feignClients.AcquirerClient;
import org.sep.enums.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final AcquirerClient acquirerClient;

    public SellerDto newPaymentMethods(SelectedPaymentMethodsDto selectedPaymentMethodsDto) {
        Optional<Seller> optionalSeller = sellerRepository.findByUsername(selectedPaymentMethodsDto.getSellerUsername());
        Seller seller;

        if (optionalSeller.isPresent()) {
            seller = optionalSeller.get();
            this.changePaymentMethods(seller, selectedPaymentMethodsDto);
        } else {
            seller = newSeller(selectedPaymentMethodsDto);
            changePaymentMethods(seller, selectedPaymentMethodsDto);
        }

        return SellerDto.builder()
                .sellerId(seller.getSellerId())
                .build();
    }


    private Seller newSeller(SelectedPaymentMethodsDto selectedPaymentMethodsDto) {
        Seller seller = new Seller();
        seller.setUsername(selectedPaymentMethodsDto.getSellerUsername());

        boolean cardOrQrPaymentSelected = selectedPaymentMethodsDto
                .getSelectedMethods().stream()
                .anyMatch((method) -> method.equals("card") || method.equals("qr"));

        if (cardOrQrPaymentSelected) {
            getSellersBankInfo(seller, selectedPaymentMethodsDto);
        }

        seller.setSellerId(generateSellerId());
        return sellerRepository.save(seller);
    }

    private Long generateSellerId() {
        return new Random().nextLong(1000, 9999);
    }

    private void getSellersBankInfo(Seller seller, SelectedPaymentMethodsDto selectedPaymentMethodsDto) {
        SellersBankInformationDto sellersInfo =
                acquirerClient.getSellersBankInfo(selectedPaymentMethodsDto.getAccountNumber()).getBody();

        seller.setMerchantId(sellersInfo.getMerchantId());
        sellersInfo.setMerchantPassword(sellersInfo.getMerchantPassword());
    }

    private void changePaymentMethods(Seller seller, SelectedPaymentMethodsDto selectedPaymentMethodsDto) {
        seller.setAvailablePaymentMethods(new HashSet<>());
        selectedPaymentMethodsDto.getSelectedMethods().forEach((method) ->
                seller.getAvailablePaymentMethods().add(PaymentMethod.valueOf(method.toUpperCase())));

        sellerRepository.save(seller);
        //todo: update roles for keycloak
    }
}
