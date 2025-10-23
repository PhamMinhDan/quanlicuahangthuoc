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
            Model model) {
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
            model.addAttribute("activeNav", "payment");
            return "payment";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách thanh toán: " + e.getMessage());
            model.addAttribute("totalPayments", paymentService.getTotalPayments());
            model.addAttribute("totalAmount", paymentService.getTotalAmount());
            model.addAttribute("totalCashPayments", paymentService.getTotalCashPayments());
            model.addAttribute("totalTransferPayments", paymentService.getTotalTransferPayments());
            return "payment";
        }
    }

    @GetMapping("/new")
    public String showAddPaymentForm(Model model) {
        model.addAttribute("payment", new Payment());
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
            Model model) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            model.addAttribute("payment", payment);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("orderId", orderId);
            model.addAttribute("paymentDate", paymentDate);
            model.addAttribute("activeNav", "payments");
            return "payment-edit";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thanh toán không tồn tại: " + e.getMessage());
            model.addAttribute("totalPayments", paymentService.getTotalPayments());
            model.addAttribute("totalAmount", paymentService.getTotalAmount());
            model.addAttribute("totalCashPayments", paymentService.getTotalCashPayments());
            model.addAttribute("totalTransferPayments", paymentService.getTotalTransferPayments());
            return listPayments(orderId, null, null, page, size, sortField, sortDir, model);
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
            @RequestParam(value = "paymentDate", required = false) LocalDate paymentDate,
            RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thanh toán thành công");
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
                (paymentDate != null ? "&paymentDate=" + paymentDate : "");
    }
}