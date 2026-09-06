package com.vendorportal.controller;

import com.vendorportal.model.PurchaseOrder;
import com.vendorportal.model.UploadedDocument;
import com.vendorportal.model.Vendor;
import com.vendorportal.repository.PurchaseOrderRepository;
import com.vendorportal.repository.UploadedDocumentRepository;
import com.vendorportal.repository.VendorRepository;
import com.vendorportal.service.FileStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/po/{poReference}")
public class PurchaseOrderController {

    private final PurchaseOrderRepository poRepository;
    private final VendorRepository vendorRepository;
    private final UploadedDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public PurchaseOrderController(PurchaseOrderRepository poRepository,
                                    VendorRepository vendorRepository,
                                    UploadedDocumentRepository documentRepository,
                                    FileStorageService fileStorageService) {
        this.poRepository = poRepository;
        this.vendorRepository = vendorRepository;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/upload")
    public String uploadForm(@PathVariable String poReference, Authentication authentication, Model model) {
        PurchaseOrder po = loadOwnedPo(poReference, authentication);
        model.addAttribute("po", po);
        return "upload"; // renders templates/upload.html
    }

    @PostMapping("/upload")
    public String submitUpload(@PathVariable String poReference,
                                Authentication authentication,
                                @RequestParam(required = false) MultipartFile invoice,
                                @RequestParam(required = false) MultipartFile deliveryOrder,
                                @RequestParam(required = false) MultipartFile coa,
                                @RequestParam(required = false) MultipartFile other,
                                @RequestParam(required = false) String remarks) {
        PurchaseOrder po = loadOwnedPo(poReference, authentication);

        saveIfPresent(po, "INVOICE", invoice);
        saveIfPresent(po, "DELIVERY_ORDER", deliveryOrder);
        saveIfPresent(po, "COA", coa);
        saveIfPresent(po, "OTHER", other);

        if (remarks != null && !remarks.isBlank()) {
            po.setRemarks(remarks);
        }

        // Mark completed once every required document type has at least one upload
        boolean allRequiredPresent = po.getRequiredDocuments().stream()
                .allMatch(type -> !documentRepository.findByPurchaseOrder(po).stream()
                        .filter(d -> d.getDocumentType().equals(type)).toList().isEmpty());
        if (allRequiredPresent) {
            po.setStatus(PurchaseOrder.PoStatus.COMPLETED);
        }
        poRepository.save(po);

        return "redirect:/summary";
    }

    private void saveIfPresent(PurchaseOrder po, String type, MultipartFile file) {
        if (file == null || file.isEmpty()) return;

        String storedFileName = fileStorageService.store(file, po.getPoReference());

        UploadedDocument doc = new UploadedDocument();
        doc.setPurchaseOrder(po);
        doc.setDocumentType(type);
        doc.setOriginalFileName(file.getOriginalFilename());
        doc.setStoredFileName(storedFileName);
        doc.setFileSizeBytes(file.getSize());
        documentRepository.save(doc);
    }

    private PurchaseOrder loadOwnedPo(String poReference, Authentication authentication) {
        Vendor vendor = vendorRepository.findByVendorCode(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return poRepository.findByPoReferenceAndVendor(poReference, vendor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PO not found for this vendor"));
    }
}
