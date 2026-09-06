package com.vendorportal.controller;

import com.vendorportal.model.PurchaseOrder;
import com.vendorportal.model.UploadedDocument;
import com.vendorportal.model.Vendor;
import com.vendorportal.repository.PurchaseOrderRepository;
import com.vendorportal.repository.UploadedDocumentRepository;
import com.vendorportal.repository.VendorRepository;
import com.vendorportal.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * JSON API backing the "Uploaded Documents" preview modal in summary.html,
 * plus a file download endpoint.
 */
@RestController
public class DocumentController {

    private final PurchaseOrderRepository poRepository;
    private final VendorRepository vendorRepository;
    private final UploadedDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public DocumentController(PurchaseOrderRepository poRepository,
                               VendorRepository vendorRepository,
                               UploadedDocumentRepository documentRepository,
                               FileStorageService fileStorageService) {
        this.poRepository = poRepository;
        this.vendorRepository = vendorRepository;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/api/po/{poReference}/documents")
    public List<Map<String, Object>> listDocuments(@PathVariable String poReference, Authentication authentication) {
        PurchaseOrder po = loadOwnedPo(poReference, authentication);
        List<UploadedDocument> docs = documentRepository.findByPurchaseOrder(po);

        return docs.stream().map(d -> Map.<String, Object>of(
                "id", d.getId(),
                "documentType", d.getDocumentType(),
                "fileName", d.getOriginalFileName(),
                "sizeMb", Math.round((d.getFileSizeBytes() / (1024.0 * 1024.0)) * 100.0) / 100.0,
                "uploadedAt", d.getUploadedAt().toString()
        )).toList();
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, Authentication authentication) {
        UploadedDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Ownership check: the document's PO must belong to the logged-in vendor
        Vendor vendor = vendorRepository.findByVendorCode(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!doc.getPurchaseOrder().getVendor().getId().equals(vendor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Path filePath = fileStorageService.resolve(doc.getPurchaseOrder().getPoReference(), doc.getStoredFileName());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File missing on disk");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Bad file path", e);
        }
    }

    private PurchaseOrder loadOwnedPo(String poReference, Authentication authentication) {
        Vendor vendor = vendorRepository.findByVendorCode(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return poRepository.findByPoReferenceAndVendor(poReference, vendor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PO not found for this vendor"));
    }
}
