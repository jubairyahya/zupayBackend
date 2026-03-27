package org.example.zupaybackend.repository;

import org.example.zupaybackend.model.Bill;
import org.example.zupaybackend.model.BillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByReferenceNumberAndBillType(
            String referenceNumber,
            BillType billType,
            String providerName
    );
}