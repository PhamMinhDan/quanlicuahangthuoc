package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/customers")
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
            Model model) {
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
            model.addAttribute("activeNav", "customers");

            return "customers";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách khách hàng: " + e.getMessage());
            return "customers";
        }
    }

@GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Customer>> getAllCustomers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        List<Customer> customers = customerService.getAllCustomers(sortBy, sortDirection);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/{id}")
    @ResponseBody
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCustomer(@PathVariable Integer id, @Valid @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok("Delete successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/paging")
    @ResponseBody
    public ResponseEntity<Page<Customer>> getAllCustomersWithPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<Customer> customers = customerService.getAllCustomersWithPagingAndSort(page, size, sortBy, direction);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/paging")
    @ResponseBody
    public ResponseEntity<Page<Customer>> searchWithPaging(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String customerType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<Customer> customers = customerService.searchWithPagingAndSort(keyword, phone, customerType, page, size, sortBy, direction);
        return ResponseEntity.ok(customers);
    }
}