package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    @GetMapping
public String getAllOrders(Model model, 
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orderPage = orderService.getAllOrders(pageable);
    model.addAttribute("orders", orderPage.getContent());
    model.addAttribute("currentPage", orderPage.getNumber());
    model.addAttribute("totalPages", orderPage.getTotalPages());
    model.addAttribute("totalItems", orderPage.getTotalElements());
    return "orders/list";
}
}
