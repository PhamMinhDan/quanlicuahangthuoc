package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.service.OrderService;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
   @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orderList = orderService.getAllOrders();
        return ResponseEntity.ok(orderList);
    }
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
    
}