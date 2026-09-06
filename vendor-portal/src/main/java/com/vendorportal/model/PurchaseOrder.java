package com.vendorportal.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String poReference;      // e.g. PO-2026-00982

    @Column(nullable = false)
    private String issuerName;       // Buyer company

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private PoStatus status = PoStatus.PENDING;

    // Which document types are required for this PO, e.g. ["INVOICE", "DELIVERY_ORDER", "COA"]
    @ElementCollection
    @CollectionTable(name = "po_required_documents", joinColumns = @JoinColumn(name = "po_id"))
    @Column(name = "document_type")
    private List<String> requiredDocuments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(length = 1000)
    private String remarks;

    public PurchaseOrder() {}

    public enum PoStatus { PENDING, COMPLETED }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPoReference() { return poReference; }
    public void setPoReference(String poReference) { this.poReference = poReference; }

    public String getIssuerName() { return issuerName; }
    public void setIssuerName(String issuerName) { this.issuerName = issuerName; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public PoStatus getStatus() { return status; }
    public void setStatus(PoStatus status) { this.status = status; }

    public List<String> getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(List<String> requiredDocuments) { this.requiredDocuments = requiredDocuments; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
