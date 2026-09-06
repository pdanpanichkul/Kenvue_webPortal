package com.vendorportal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vendorCode;      // e.g. VEND-8891 (login id)

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String passwordHash;    // BCrypt hash, never store plain text

    public Vendor() {}

    public Vendor(String vendorCode, String email, String companyName, String passwordHash) {
        this.vendorCode = vendorCode;
        this.email = email;
        this.companyName = companyName;
        this.passwordHash = passwordHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
