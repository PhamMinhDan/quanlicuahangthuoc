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
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
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
            model.addAttribute("message", "Thêm thanh toán thành công");
            return "redirect:/payments/view-payments";
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
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
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
            // Format paymentDate to String matching parseDate format
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
            model.addAttribute("message", "Cập nhật thanh toán thành công");
            return "redirect:/payments/view-payments";
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
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        // Kiểm tra vai trò quan_ly
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa thanh toán.");
            return "redirect:/payments/view-payments?page=" + page +
                    "&size=" + size +
                    "&sortField=" + sortField +
                    "&sortDir=" + sortDir +
                    (orderId != null ? "&orderId=" + orderId : "") +
                    (fromDateStr != null ? "&fromDate=" + fromDateStr : "") +
                    (toDateStr != null ? "&toDate=" + toDateStr : "");
        }

        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thanh toán thành công");

            // Parse dates from dd/MM/yyyy format
            LocalDate fromDate = paymentService.parseDate(fromDateStr);
            LocalDate toDate = paymentService.parseDate(toDateStr);

            // Tính tổng số thanh toán sau khi xóa với bộ lọc
            long totalItems = paymentService.getTotalPaymentsWithFilters(orderId, fromDate, toDate);
            int totalPages = (int) Math.ceil((double) totalItems / size);

            // Điều chỉnh page nếu trang hiện tại lớn hơn hoặc bằng tổng số trang
            int adjustedPage = page;
            if (page >= totalPages && totalPages > 0) {
                adjustedPage = totalPages - 1;
            } else if (totalPages == 0) {
                adjustedPage = 0;
            }

            return "redirect:/payments/view-payments?page=" + adjustedPage +
                    "&size=" + size +
                    "&sortField=" + sortField +
                    "&sortDir=" + sortDir +
                    (orderId != null ? "&orderId=" + orderId : "") +
                    (fromDateStr != null ? "&fromDate=" + fromDateStr : "") +
                    (toDateStr != null ? "&toDate=" + toDateStr : "");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Thanh toán không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi server: " + e.getMessage());
        }
        return "redirect:/payments/view-payments?page=" + page +
                "&size=" + size +
                "&sortField=" + sortField +
                "&sortDir=" + sortDir +
                (orderId != null ? "&orderId=" + orderId : "") +
                (fromDateStr != null ? "&fromDate=" + fromDateStr : "") +
                (toDateStr != null ? "&toDate=" + toDateStr : "");
    }
}