package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Page<OrderDetail> getPagedOrderDetails(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return orderDetailRepository.findAll(pageable);
    }

    // Ví dụ các hàm thống kê
    public java.math.BigDecimal getTotalRevenue() {
        return orderDetailRepository.findAll().stream()
                .map(OrderDetail::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public Integer getTotalProductsSold() {
        return orderDetailRepository.findAll().stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }
}
