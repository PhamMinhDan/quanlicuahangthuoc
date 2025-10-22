package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    
    @GetMapping("/create")
    public String createOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "orders/create";
    }
    
    @PostMapping("/create")
    public String createOrder(@ModelAttribute Order order) {
        orderService.addOrder(order);
        return "redirect:/orders";
    }
    
    @GetMapping("/edit/{id}")
    public String editOrderForm(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            model.addAttribute("order", order);
            return "orders/edit";
        }
        return "redirect:/orders";
    }
    
    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable Integer id, @ModelAttribute Order order) {
        orderService.updateOrder(id, order);
        return "redirect:/orders";
    }
}