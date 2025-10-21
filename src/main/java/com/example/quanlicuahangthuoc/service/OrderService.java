package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Page<Order> getOrdersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }
    public Page<Order> getOrdersByPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return orderRepository.findAll(pageable);
    }
    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }
}