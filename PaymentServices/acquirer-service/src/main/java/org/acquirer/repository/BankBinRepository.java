package org.acquirer.repository;

import org.acquirer.model.BankBin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankBinRepository extends JpaRepository<BankBin, Long> {
    Optional<BankBin> findByBin(String bin);
}
