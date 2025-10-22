package com.example.quanlicuahangthuoc.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.service.OrderService;
@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/search")
    public String searchOrders(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String orderDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Model model
    ) {
        // Xử lý giá trị nhập vào
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;

        LocalDate date = null;
        if (orderDate != null && !orderDate.isBlank()) {
            try {
                date = LocalDate.parse(orderDate);
            } catch (Exception e) {
                model.addAttribute("error", "Định dạng ngày không hợp lệ. Dùng yyyy-MM-dd");
            }
        }

        Order.OrderStatus st = null;
        if (status != null && !status.isBlank()) {
            try {
                st = Order.OrderStatus.valueOf(status);
            } catch (Exception e) {
                model.addAttribute("error", "Trạng thái không hợp lệ. Các giá trị hợp lệ: da_thanh_toan, chua_thanh_toan, da_huy");
            }
        }

        var pageResult = orderService.searchOrders(customerId, date, st, p, s);

        // Truyền dữ liệu ra view
        model.addAttribute("orders", pageResult.getContent());
        model.addAttribute("currentPage", p);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("customerId", customerId);
        model.addAttribute("orderDate", orderDate);
        model.addAttribute("status", status);

        return "orders/search"; // templates/orders/search.html
    }
    
    
}