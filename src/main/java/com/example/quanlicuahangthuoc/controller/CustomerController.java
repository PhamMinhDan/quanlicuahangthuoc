package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<Customer> customerPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            customerPage = customerService.searchWithPagingAndSort(keyword, page, size, sortBy, direction);
            model.addAttribute("keyword", keyword);
        } else {
            customerPage = customerService.getAllCustomersWithPagingAndSort(page, size, sortBy, direction);
        }

        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalItems", customerPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "customer/list";
    }
}