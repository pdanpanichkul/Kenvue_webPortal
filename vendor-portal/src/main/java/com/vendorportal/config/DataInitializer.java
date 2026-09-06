package com.vendorportal.config;

import com.vendorportal.model.PurchaseOrder;
import com.vendorportal.model.PurchaseOrder.PoStatus;
import com.vendorportal.model.UploadedDocument;
import com.vendorportal.model.Vendor;
import com.vendorportal.repository.PurchaseOrderRepository;
import com.vendorportal.repository.UploadedDocumentRepository;
import com.vendorportal.repository.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Populates the database with one demo vendor and two demo purchase orders,
 * matching the original mock data, so the app is immediately runnable/testable.
 * Safe to delete once you have real vendor/PO data flowing in from elsewhere.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository poRepository;
    private final UploadedDocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(VendorRepository vendorRepository,
                            PurchaseOrderRepository poRepository,
                            UploadedDocumentRepository documentRepository,
                            PasswordEncoder passwordEncoder) {
        this.vendorRepository = vendorRepository;
        this.poRepository = poRepository;
        this.documentRepository = documentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (vendorRepository.count() > 0) {
            return; // already seeded
        }

        Vendor vendor = new Vendor(
                "VEND-8891",
                "vendor@abc.com",
                "ABC Logistics Co., Ltd.",
                passwordEncoder.encode("password123") // demo password only - change in production
        );
        vendorRepository.save(vendor);

        PurchaseOrder pending = new PurchaseOrder();
        pending.setPoReference("PO-2026-00982");
        pending.setIssuerName("Global Tech Corp Thailand");
        pending.setDeadline(LocalDate.of(2026, 9, 15));
        pending.setStatus(PoStatus.PENDING);
        pending.setRequiredDocuments(List.of("INVOICE", "DELIVERY_ORDER", "COA"));
        pending.setVendor(vendor);
        poRepository.save(pending);

        PurchaseOrder completed = new PurchaseOrder();
        completed.setPoReference("PO-2026-00754");
        completed.setIssuerName("Global Tech Corp Thailand");
        completed.setDeadline(LocalDate.of(2026, 8, 30));
        completed.setStatus(PoStatus.COMPLETED);
        completed.setRequiredDocuments(List.of("INVOICE", "DELIVERY_ORDER"));
        completed.setVendor(vendor);
        poRepository.save(completed);

        UploadedDocument invoice = new UploadedDocument();
        invoice.setPurchaseOrder(completed);
        invoice.setDocumentType("INVOICE");
        invoice.setOriginalFileName("INV-9923_TaxInvoice.pdf");
        invoice.setStoredFileName("seed-INV-9923_TaxInvoice.pdf");
        invoice.setFileSizeBytes(1_200_000);
        documentRepository.save(invoice);

        UploadedDocument deliveryOrder = new UploadedDocument();
        deliveryOrder.setPurchaseOrder(completed);
        deliveryOrder.setDocumentType("DELIVERY_ORDER");
        deliveryOrder.setOriginalFileName("DO-00441_DeliveryOrder.png");
        deliveryOrder.setStoredFileName("seed-DO-00441_DeliveryOrder.png");
        deliveryOrder.setFileSizeBytes(845_000);
        documentRepository.save(deliveryOrder);
    }
}
