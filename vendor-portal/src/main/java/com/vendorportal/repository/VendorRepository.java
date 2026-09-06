package com.vendorportal.repository;

import com.vendorportal.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByVendorCode(String vendorCode);
    Optional<Vendor> findByEmail(String email);
}
