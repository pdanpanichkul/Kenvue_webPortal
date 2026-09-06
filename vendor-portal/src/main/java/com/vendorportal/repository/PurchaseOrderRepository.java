package com.vendorportal.repository;

import com.vendorportal.model.PurchaseOrder;
import com.vendorportal.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByVendor(Vendor vendor);
    Optional<PurchaseOrder> findByPoReference(String poReference);
    Optional<PurchaseOrder> findByPoReferenceAndVendor(String poReference, Vendor vendor);
}
