package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/view-customers")
    public String viewCustomers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            Model model,
            Authentication authentication) {
        try {
            // Chỉ cho phép sắp xếp theo "name" hoặc "rewardPoints"
            if (!sortBy.equals("name") && !sortBy.equals("rewardPoints")) {
                sortBy = "name";
            }
            
            // Trim và loại bỏ khoảng trắng thừa từ keyword và phone
            String cleanKeyword = null;
            if (keyword != null && !keyword.trim().isEmpty()) {
                cleanKeyword = keyword.trim().replaceAll("\\s+", " ");
            }
            String cleanPhone = null;
            if (phone != null && !phone.trim().isEmpty()) {
                cleanPhone = phone.trim().replaceAll("\\s+", " ");
            }
            
            Page<Customer> customerPage;
            if (cleanKeyword != null || cleanPhone != null || customerType != null) {
                customerPage = customerService.searchWithPagingAndSort(cleanKeyword, cleanPhone, customerType, page, size, sortBy, sortDirection);
            } else {
                customerPage = customerService.getAllCustomersWithPagingAndSort(page, size, sortBy, sortDirection);
            }

            List<Customer> customers = customerPage.getContent();
            model.addAttribute("customers", customers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", cleanKeyword);
            model.addAttribute("phone", cleanPhone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("totalItems", customerPage.getTotalElements());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "customers");
            return "customers";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách khách hàng: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "customers");
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
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model) {
        if (bindingResult.hasErrors()) {
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
            customerService.createCustomer(customer);

            // THÊM MỚI: Quay về trang đầu tiên, KHÔNG GIỮ filter
            return "redirect:/customers/view-customers?page=0" +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection +
                    "&addSuccess=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("activeNav", "customers");
            return "customer-form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
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

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model,
            Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            model.addAttribute("error", "Bạn không có quyền chỉnh sửa khách hàng.");
            model.addAttribute("keyword", keyword);
            model.addAttribute("phone", phone);
            model.addAttribute("customerType", customerType);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "customers");
            return viewCustomers(keyword, phone, customerType, page, size, sortBy, sortDirection, model, authentication);
        }

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
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "customers");
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
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model,
            Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            model.addAttribute("error", "Bạn không có quyền chỉnh sửa khách hàng.");
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

        if (bindingResult.hasErrors()) {
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
            customer.setId(id);
            customerService.updateCustomer(id, customer);

            // CẬP NHẬT: Quay về trang đầu tiên (page=0), bỏ các tham số lọc
            return "redirect:/customers/view-customers?page=0" +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection +
                    "&updateSuccess=true";
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
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
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
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerType", required = false) String customerType,
            Model model,
            Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            model.addAttribute("error", "Bạn không có quyền xóa khách hàng.");
            StringBuilder redirectUrl = new StringBuilder("/customers/view-customers?page=" + page +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection);
            if (keyword != null && !keyword.isEmpty()) {
                redirectUrl.append("&keyword=").append(keyword);
            }
            if (phone != null && !phone.isEmpty()) {
                redirectUrl.append("&phone=").append(phone);
            }
            if (customerType != null && !customerType.isEmpty()) {
                redirectUrl.append("&customerType=").append(customerType);
            }
            return "redirect:" + redirectUrl.toString();
        }

        try {
            Page<Customer> customerPage = customerService.searchWithPagingAndSort(keyword, phone, customerType, page, size, sortBy, sortDirection);
            int totalItemsOnPage = customerPage.getNumberOfElements();
            customerService.deleteCustomer(id);
            customerPage = customerService.searchWithPagingAndSort(keyword, phone, customerType, page, size, sortBy, sortDirection);
            int newTotalItemsOnPage = customerPage.getNumberOfElements();
            int newPage = page;
            if (totalItemsOnPage == 1 && newTotalItemsOnPage == 0 && page > 0) {
                newPage = page - 1;
            }
            StringBuilder redirectUrl = new StringBuilder("/customers/view-customers?page=" + newPage +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection +
                    "&deleteSuccess=true");
            if (keyword != null && !keyword.isEmpty()) {
                redirectUrl.append("&keyword=").append(keyword);
            }
            if (phone != null && !phone.isEmpty()) {
                redirectUrl.append("&phone=").append(phone);
            }
            if (customerType != null && !customerType.isEmpty()) {
                redirectUrl.append("&customerType=").append(customerType);
            }
            return "redirect:" + redirectUrl.toString();
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Khách hàng không tồn tại: " + e.getMessage());
            StringBuilder redirectUrl = new StringBuilder("/customers/view-customers?page=" + page +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection);
            if (keyword != null && !keyword.isEmpty()) {
                redirectUrl.append("&keyword=").append(keyword);
            }
            if (phone != null && !phone.isEmpty()) {
                redirectUrl.append("&phone=").append(phone);
            }
            if (customerType != null && !customerType.isEmpty()) {
                redirectUrl.append("&customerType=").append(customerType);
            }
            return "redirect:" + redirectUrl.toString();
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
            StringBuilder redirectUrl = new StringBuilder("/customers/view-customers?page=" + page +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection);
            if (keyword != null && !keyword.isEmpty()) {
                redirectUrl.append("&keyword=").append(keyword);
            }
            if (phone != null && !phone.isEmpty()) {
                redirectUrl.append("&phone=").append(phone);
            }
            if (customerType != null && !customerType.isEmpty()) {
                redirectUrl.append("&customerType=").append(customerType);
            }
            return "redirect:" + redirectUrl.toString();
        }
    }
}