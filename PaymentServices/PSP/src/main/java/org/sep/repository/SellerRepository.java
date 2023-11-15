package org.sep.repository;


import org.sep.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findBySellerId(Long sellerId);
}
