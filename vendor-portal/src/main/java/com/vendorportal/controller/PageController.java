package com.vendorportal.controller;

import com.vendorportal.model.PurchaseOrder;
import com.vendorportal.model.Vendor;
import com.vendorportal.repository.PurchaseOrderRepository;
import com.vendorportal.repository.VendorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository poRepository;

    public PageController(VendorRepository vendorRepository, PurchaseOrderRepository poRepository) {
        this.vendorRepository = vendorRepository;
        this.poRepository = poRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // renders templates/login.html; Spring Security handles the POST
    }

    @GetMapping({"/", "/summary"})
    public String summaryPage(Authentication authentication, Model model) {
        Vendor vendor = currentVendor(authentication);
        List<PurchaseOrder> orders = poRepository.findByVendor(vendor);

        model.addAttribute("vendor", vendor);
        model.addAttribute("purchaseOrders", orders);
        return "summary"; // renders templates/summary.html
    }

    private Vendor currentVendor(Authentication authentication) {
        String vendorCode = authentication.getName();
        return vendorRepository.findByVendorCode(vendorCode)
                .orElseThrow(() -> new IllegalStateException("Logged in vendor not found: " + vendorCode));
    }
}
