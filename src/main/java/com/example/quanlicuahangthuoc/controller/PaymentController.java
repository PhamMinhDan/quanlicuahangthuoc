package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Payment;
import com.example.quanlicuahangthuoc.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payment/list";
    }

    @GetMapping("/{id}")
    public String viewPayment(@PathVariable Integer id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        model.addAttribute("payment", payment);
        return "payment/detail";
    }
    @GetMapping
    public String listPayments(
            @RequestParam(value = "sortField", defaultValue = "paymentDate") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        List<Payment> payments = paymentService.getAllPayments();

        // Xác định hướng sắp xếp tiếp theo (đảo ngược cho lần click kế)
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("payments", payments);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        return "payment/list";
    }
    @GetMapping("/page")
    public String listPaymentsPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<Payment> paymentPage = paymentService.getPaymentsPaged(page, size);
        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        return "payment/list";
    }
    // Hiển thị form thêm thanh toán mới
    @GetMapping("/new")
    public String showAddPaymentForm(Model model) {
        model.addAttribute("payment", new Payment());
        return "payment/add"; // trỏ đến file payment/add.html
    }

    // Xử lý khi người dùng submit form
    @PostMapping("/save")
    public String savePayment(@ModelAttribute("payment") Payment payment) {
        paymentService.savePayment(payment);
        return "redirect:/payments"; // Sau khi thêm, quay lại danh sách
    }
    // Hiển thị form chỉnh sửa thanh toán
    @GetMapping("/edit/{id}")
    public String showEditPaymentForm(@PathVariable Integer id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        model.addAttribute("payment", payment);
        return "payment/edit"; // Trỏ tới file templates/payment/edit.html
    }

    // Xử lý khi người dùng nhấn “Lưu”
    @PostMapping("/update/{id}")
    public String updatePayment(@PathVariable Integer id, @ModelAttribute("payment") Payment payment) {
        paymentService.updatePayment(id, payment);
        return "redirect:/payments";
    }
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("message", "Xoá thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xoá thanh toán: " + e.getMessage());
        }
        return "redirect:/payments"; // quay lại danh sách thanh toán
    }

}
