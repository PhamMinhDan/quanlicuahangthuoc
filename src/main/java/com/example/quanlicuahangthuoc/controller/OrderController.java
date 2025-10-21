package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Integer id, 
            @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchOrders(
            Integer customerId,
            String orderDate,
            String status,
            Integer page,
            Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;
        java.time.LocalDate date = null;
        if (orderDate != null && !orderDate.isBlank()) {
            try {
                date = java.time.LocalDate.parse(orderDate);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM-dd");
            }
        }
        Order.OrderStatus st = null;
        if (status != null && !status.isBlank()) {
            try {
                st = Order.OrderStatus.valueOf(status);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: da_thanh_toan, chua_thanh_toan, da_huy");
            }
        }
        var pageResult = orderService.searchOrders(customerId, date, st, p, s);
        return ResponseEntity.ok(pageResult);
    }
    
}