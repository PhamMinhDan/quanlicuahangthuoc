package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            model.addAttribute("customerTypes", Customer.CustomerType.values());
            return "customer/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(
            @PathVariable Integer id,
            @Valid @ModelAttribute Customer customer,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            model.addAttribute("customerTypes", Customer.CustomerType.values());
            return "customer/form";
        }

        try {
            customerService.updateCustomer(id, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
            return "redirect:/customers";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("customer", customer);
            model.addAttribute("customerTypes", Customer.CustomerType.values());
            return "customer/form";
        }
    }
}