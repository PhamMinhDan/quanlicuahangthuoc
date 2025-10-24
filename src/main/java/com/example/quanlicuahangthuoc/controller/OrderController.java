package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Hiển thị danh sách đơn hàng với tìm kiếm bằng GET
    @GetMapping("/view-orders")
    public String viewOrders(
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "orderDate", required = false) LocalDate orderDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            Model model, Authentication authentication) {
        try {
            Order.OrderStatus orderStatus = (status != null && !status.trim().isEmpty()) ?
                    Order.OrderStatus.valueOf(status) : null;
            Page<Order> orderPage = orderService.getOrderPage(customerId, orderDate, orderStatus, page, size, sortBy, sortDirection);

            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("customerId", customerId);
            model.addAttribute("orderDate", orderDate);
            model.addAttribute("status", status);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "orders");
            return "order";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            return "order";
        }
    }

    // Hiển thị form thêm đơn hàng
    @GetMapping("/add")
    public String showAddOrderForm(Model model, Authentication authentication) {
        model.addAttribute("order", new Order());
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
        model.addAttribute("activeNav", "orders");
        return "order-form";
    }

    // Xử lý thêm đơn hàng
    @PostMapping("/add")
    public String createOrder(
            @Valid @ModelAttribute Order order,
            BindingResult bindingResult,
            Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu"));
            model.addAttribute("order", order);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "orders");
            return "order-form";
        }
        try {
            orderService.addOrder(order);
            model.addAttribute("message", "Thêm đơn hàng thành công");
            return "redirect:/orders/view-orders";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("order", order);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "orders");
            return "order-form";
        }
    }

    // Hiển thị form sửa đơn hàng
    @GetMapping("/edit/{id}")
    public String showEditOrderForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "orderDate", required = false) LocalDate orderDate,
            @RequestParam(value = "status", required = false) String status,
            Model model, Authentication authentication) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("customerId", customerId);
            model.addAttribute("orderDate", orderDate);
            model.addAttribute("status", status);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "orders");
            return "order-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Đơn hàng không tồn tại: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            return "order";
        }
    }

    // Xử lý sửa đơn hàng
    @PostMapping("/update/{id}")
    public String updateOrder(
            @PathVariable Integer id,
            @Valid @ModelAttribute Order order,
            BindingResult bindingResult,
            Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu"));
            model.addAttribute("order", order);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "orders");
            return "order-form";
        }
        try {
            order.setId(id);
            orderService.updateOrder(order);
            model.addAttribute("message", "Cập nhật đơn hàng thành công");
            return "redirect:/orders/view-orders";
        } catch (IllegalStateException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("order", order);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("isManagerOrEmployee", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly") || auth.getAuthority().equals("ROLE_nhan_vien")));
            model.addAttribute("activeNav", "orders");
            return "order-form";
        }
    }

    // Xử lý xóa đơn hàng
    @PostMapping("/delete/{id}")
    public String deleteOrder(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "orderDate", required = false) LocalDate orderDate,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        try {
            orderService.deleteOrder(id);
            model.addAttribute("message", "Xóa đơn hàng thành công");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Đơn hàng không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
        }
        return "redirect:/orders/view-orders?page=" + page +
                "&size=" + size +
                "&sortBy=" + sortBy +
                "&sortDirection=" + sortDirection +
                (customerId != null ? "&customerId=" + customerId : "") +
                (orderDate != null ? "&orderDate=" + orderDate : "") +
                (status != null ? "&status=" + status : "");
    }
}