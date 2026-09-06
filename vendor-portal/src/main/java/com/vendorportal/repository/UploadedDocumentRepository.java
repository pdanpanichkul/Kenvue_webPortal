package com.vendorportal.repository;

import com.vendorportal.model.PurchaseOrder;
import com.vendorportal.model.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, Long> {
    List<UploadedDocument> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
