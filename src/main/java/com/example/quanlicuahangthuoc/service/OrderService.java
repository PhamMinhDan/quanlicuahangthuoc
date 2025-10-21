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
    public Order updateOrder(Integer id, Order order) {
        return orderRepository.findById(id).map(existingOrder -> {
            existingOrder.setOrderDate(order.getOrderDate());
            existingOrder.setCustomer(order.getCustomer());
            existingOrder.setStaff(order.getStaff());
            existingOrder.setPromotion(order.getPromotion());
            existingOrder.setTotalAmount(order.getTotalAmount());
            return orderRepository.save(existingOrder);
        }).orElse(null);
    }
    public List<Order> getOrdersById(Integer id) {
        return orderRepository.findByOrderId(id);
    }
    public Page<Order> getOrdersByIdPaginated(Integer id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByOrderId(id, pageable);
    }

    public Page<Order> searchOrders(Integer customerId, java.time.LocalDate orderDate, Order.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.searchOrders(customerId, orderDate, status, pageable);
    }
}