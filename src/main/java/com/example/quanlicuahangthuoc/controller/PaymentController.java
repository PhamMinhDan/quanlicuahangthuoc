package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Payment;
import com.example.quanlicuahangthuoc.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/view-payments")
    public String listPayments(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir, // Mặc định là desc
            Model model, Authentication authentication) {
        try {
            // Parse dates from dd/MM/yyyy format
            LocalDate fromDate = paymentService.parseDate(fromDateStr);
            LocalDate toDate = paymentService.parseDate(toDateStr);

            Page<Payment> paymentPage = paymentService.getPaymentsPaged(orderId, fromDate, toDate, page, size, sortField, sortDir);
            model.addAttribute("payments", paymentPage.getContent());
            model.addAttribute("totalItems", paymentPage.getTotalElements());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paymentPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("orderId", orderId);
            model.addAttribute("fromDate", fromDateStr);
            model.addAttribute("toDate", toDateStr);
            model.addAttribute("totalPayments", paymentService.getTotalPayments());
            model.addAttribute("totalAmount", paymentService.getTotalAmount());
            model.addAttribute("totalCashPayments", paymentService.getTotalCashPayments());
            model.addAttribute("totalTransferPayments", paymentService.getTotalTransferPayments());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "payments");
            return "payment";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách thanh toán: " + e.getMessage());
            model.addAttribute("totalPayments", paymentService.getTotalPayments());
            model.addAttribute("totalAmount", paymentService.getTotalAmount());
            model.addAttribute("totalCashPayments", paymentService.getTotalCashPayments());
            model.addAttribute("totalTransferPayments", paymentService.getTotalTransferPayments());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            return "payment";
        }
    }

    @GetMapping("/new")
    public String showAddPaymentForm(Model model, Authentication authentication) {
        model.addAttribute("payment", new Payment());
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));

        model.addAttribute("activeNav", "payments");
        return "payment-add";
    }

    @PostMapping("/save")
    public String savePayment(
            @Valid @ModelAttribute Payment payment,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu"));
            model.addAttribute("payment", payment);
            model.addAttribute("activeNav", "payments");
            return "payment-add";
        }
        try {
            paymentService.savePayment(payment);
            // THÊM addSuccess=true
            return "redirect:/payments/view-payments?page=0&size=10&sortField=id&sortDir=desc&addSuccess=true";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm thanh toán: " + e.getMessage());
            model.addAttribute("payment", payment);
            model.addAttribute("activeNav", "payments");
            return "payment-add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditPaymentForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir, // Mặc định là desc
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "paymentDate", required = false) LocalDate paymentDate,
            Model model, Authentication authentication) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            model.addAttribute("payment", payment);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("orderId", orderId);
            model.addAttribute("paymentDate", paymentDate);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "payments");
            return "payment-edit";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thanh toán không tồn tại: " + e.getMessage());
            model.addAttribute("totalPayments", paymentService.getTotalPayments());
            model.addAttribute("totalAmount", paymentService.getTotalAmount());
            model.addAttribute("totalCashPayments", paymentService.getTotalCashPayments());
            model.addAttribute("totalTransferPayments", paymentService.getTotalTransferPayments());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            String fromDateStr = paymentDate != null ? paymentDate.format(paymentService.getDateFormatter()) : null;
            String toDateStr = paymentDate != null ? paymentDate.format(paymentService.getDateFormatter()) : null;
            return listPayments(orderId, fromDateStr, toDateStr, page, size, sortField, sortDir, model, authentication);
        }
    }

    @PostMapping("/update/{id}")
    public String updatePayment(
            @PathVariable Integer id,
            @Valid @ModelAttribute Payment payment,
            BindingResult bindingResult,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu"));
            model.addAttribute("payment", payment);
            model.addAttribute("activeNav", "payments");
            return "payment-edit";
        }
        try {
            paymentService.updatePayment(id, payment);
            // THÊM updateSuccess=true và giữ lại các parameters
            return buildRedirectUrl(page, size, sortField, sortDir, orderId, fromDate, toDate, "updateSuccess");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thanh toán không tồn tại: " + e.getMessage());
            model.addAttribute("payment", payment);
            model.addAttribute("activeNav", "payments");
            return "payment-edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePayment(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa thanh toán.");
            return buildRedirectUrl(page, size, sortField, sortDir, orderId, fromDateStr, toDateStr, null);
        }

        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thanh toán thành công");

            LocalDate fromDate = paymentService.parseDate(fromDateStr);
            LocalDate toDate = paymentService.parseDate(toDateStr);

            long totalItems = paymentService.getTotalPaymentsWithFilters(orderId, fromDate, toDate);
            int totalPages = (int) Math.ceil((double) totalItems / size);

            int adjustedPage = page;
            if (page >= totalPages && totalPages > 0) {
                adjustedPage = totalPages - 1;
            } else if (totalPages == 0) {
                adjustedPage = 0;
            }

            return buildRedirectUrl(adjustedPage, size, sortField, sortDir, orderId, fromDateStr, toDateStr, "deleteSuccess");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Thanh toán không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi server: " + e.getMessage());
        }

        return buildRedirectUrl(page, size, sortField, sortDir, orderId, fromDateStr, toDateStr, null);
    }

    private String buildRedirectUrl(int page, int size, String sortField, String sortDir,
                                    Integer orderId, String fromDate, String toDate, String successParam) {
        StringBuilder url = new StringBuilder("redirect:/payments/view-payments?page=" + page);
        url.append("&size=").append(size);
        url.append("&sortField=").append(sortField);
        url.append("&sortDir=").append(sortDir);

        if (orderId != null) {
            url.append("&orderId=").append(orderId);
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            url.append("&fromDate=").append(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            url.append("&toDate=").append(toDate);
        }
        if (successParam != null) {
            url.append("&").append(successParam).append("=true");
        }

        return url.toString();
    }
}