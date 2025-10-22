package com.example.quanlicuahangthuoc.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    public String searchOrders(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String orderDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model
    ) {
        
        LocalDate date = null;
        if (orderDate != null && !orderDate.isBlank()) {
            try {
                date = LocalDate.parse(orderDate);
            } catch (Exception e) {
                model.addAttribute("error", "Ngày không hợp lệ, định dạng đúng là yyyy-MM-dd");
                return "orders/list";
            }
        }

        Order.OrderStatus st = null;
        if (status != null && !status.isBlank()) {
            try {
                st = Order.OrderStatus.valueOf(status);
            } catch (Exception e) {
                model.addAttribute("error", "Trạng thái không hợp lệ!");
                return "orders/list";
            }
        }

        String sortField = switch (sortBy.toLowerCase()) {
            case "total", "totalamount", "amount" -> "totalAmount";
            default -> "orderDate";
        };

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        
        Page<Order> result = orderService.searchOrders(customerId, date, st, pageable);

        model.addAttribute("orders", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "orders/list"; // → resources/templates/orders/list.html
    }
}