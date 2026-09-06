package com.vendorportal.service;

import com.vendorportal.model.Vendor;
import com.vendorportal.repository.VendorRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Bridges Spring Security's login mechanism to our Vendor table.
 * A vendor can log in with either their vendorCode or their email address,
 * matching the "Vendor ID / Email" field on the login page.
 */
@Service
public class VendorUserDetailsService implements UserDetailsService {

    private final VendorRepository vendorRepository;

    public VendorUserDetailsService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Vendor vendor = vendorRepository.findByVendorCode(usernameOrEmail)
                .or(() -> vendorRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("No vendor found for: " + usernameOrEmail));

        return User.builder()
                .username(vendor.getVendorCode())
                .password(vendor.getPasswordHash())
                .roles("VENDOR")
                .build();
    }
}
