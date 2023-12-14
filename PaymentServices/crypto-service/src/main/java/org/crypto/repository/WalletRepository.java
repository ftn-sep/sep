package org.crypto.repository;

import org.crypto.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    public Optional<Wallet> findByMerchantId(String merchantId);
}
