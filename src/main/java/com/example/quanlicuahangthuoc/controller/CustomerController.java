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

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("customerTypes", Customer.CustomerType.values());
        return "customer/form";
    }

    @PostMapping
    public String createCustomer(
            @Valid @ModelAttribute Customer customer,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("customerTypes", Customer.CustomerType.values());
            return "customer/form";
        }

        try {
            customerService.createCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo khách hàng thành công!");
            return "redirect:/customers";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("customerTypes", Customer.CustomerType.values());
            return "customer/form";
        }
    }
}
