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
    public String getAllOrders(Model model) {
        List<Order> orderList = orderService.getAllOrders();
        model.addAttribute("orders", orderList);
        return "orders/list"; // tên file HTML trong templates
    }
}