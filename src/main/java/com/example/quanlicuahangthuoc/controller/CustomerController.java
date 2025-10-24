package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/view-customers")
    public String viewCustomers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            Model model,
            Authentication authentication) {
        try {
            Page<Customer> customerPage;
            if ((keyword != null && !keyword.trim().isEmpty()) ||
                    (phone != null && !phone.trim().isEmpty()) ||
                    (customerType != null && !customerType.trim().isEmpty())) {
                customerPage = customerService.searchWithPagingAndSort(keyword, phone, customerType, page, size, sortBy, sortDirection);
            } else {
                customerPage = customerService.getAllCustomersWithPagingAndSort(page, size, sortBy, sortDirection);
            }

            model.addAttribute("customers", customerPage.getContent());
            model.addAttribute("currentPage", customerPage.getNumber());
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("totalCustomers", customerService.getTotalCustomers());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "customers");
            return "customers";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách khách hàng: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            return "customers";
        }
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model, Authentication authentication) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "customers");
        return "customer-form";
    }

    @PostMapping("/add")
    public String createCustomer(
            @Valid @ModelAttribute Customer customer,
            BindingResult bindingResult,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu");
            model.addAttribute("error", errorMessage);
            model.addAttribute("customer", customer);
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        }

        try {
            customerService.createCustomer(customer);
            model.addAttribute("message", "Thêm khách hàng thành công");
            return "redirect:/customers/view-customers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("customer", customer);
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model,
            Authentication authentication) {
        try {
            Customer customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Khách hàng không tồn tại: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            return viewCustomers(keyword, phone, customerType, page, size, sortBy, sortDirection, model, authentication);
        }
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(
            @PathVariable Integer id,
            @Valid @ModelAttribute Customer customer,
            BindingResult bindingResult,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu");
            model.addAttribute("error", errorMessage);
            model.addAttribute("customer", customer);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        }

        try {
            customerService.updateCustomer(id, customer);
            model.addAttribute("message", "Cập nhật khách hàng thành công");
            return "redirect:/customers/view-customers";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Khách hàng không tồn tại: " + e.getMessage());
            model.addAttribute("customer", customer);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("customer", customer);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model) {
        try {
            customerService.deleteCustomer(id);
            model.addAttribute("message", "Xóa khách hàng thành công");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Khách hàng không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
        }
        return "redirect:/customers/view-customers";
    }
}