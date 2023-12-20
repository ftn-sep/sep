package org.psp.service;

import lombok.RequiredArgsConstructor;
import org.psp.dto.PaymentMethodsDto;
import org.psp.dto.SelectedPaymentMethodsDto;
import org.psp.dto.SellerDto;
import org.psp.dto.SellersBankInformationDto;
import org.psp.model.Seller;
import org.psp.repository.SellerRepository;
import org.psp.service.feignClients.AcquirerClient;
import org.sep.enums.PaymentMethod;
import org.sep.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.*;

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

        if (checkIfBankInfoNeeded(seller)) {
            getSellersBankInfo(seller, selectedPaymentMethodsDto);
        }
        sellerRepository.save(seller);
    }

    private boolean checkIfBankInfoNeeded(Seller seller) {
        if (seller.getMerchantId() != null) return false; // psp already has bank info for this seller

        // if seller selected QR or CARD for the first time
        // we need to check his bank info from acquirer
        return seller.getAvailablePaymentMethods().stream()
                .anyMatch(method -> method.equals(PaymentMethod.CARD) || method.equals(PaymentMethod.QR));
    }

    public PaymentMethodsDto getSubscribedPaymentMethods(String merchantOrderId, String username) {
        Optional<Seller> optionalSeller;
        if (merchantOrderId != null) {
            Long sellerId = Long.valueOf(merchantOrderId.substring(0, 4));
            optionalSeller = sellerRepository.findBySellerId(sellerId);
        } else if (username != null) {
            optionalSeller = sellerRepository.findByUsername(username);
        } else {
            throw new BadRequestException("There is no merchantOrderId or merchantUsername passed");
        }
        Set<PaymentMethod> paymentMethods = (optionalSeller.isEmpty()) ? new HashSet<>() : optionalSeller.get().getAvailablePaymentMethods();
        boolean hasMerchantIdAndPassword = optionalSeller.isPresent() && optionalSeller.get().getMerchantId() != null;
        return new PaymentMethodsDto(paymentMethods, hasMerchantIdAndPassword);
    }
}
