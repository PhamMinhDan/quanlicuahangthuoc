package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Lấy tất cả order details
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    // Lấy order detail theo ID
    public Optional<OrderDetail> getOrderDetailById(Integer id) {
        return orderDetailRepository.findById(id);
    }

    // Lấy order details theo order ID
    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    // Lấy order details theo medicine ID
    public List<OrderDetail> getOrderDetailsByMedicineId(Integer medicineId) {
        return orderDetailRepository.findByMedicineId(medicineId);
    }

    // Tìm kiếm order details
    public List<OrderDetail> searchOrderDetails(Integer orderId, String medicineName, String customerName) {
        return orderDetailRepository.searchOrderDetails(orderId, medicineName, customerName);
    }

    // Tính tổng doanh thu từ order details
    public BigDecimal getTotalRevenue() {
        return getAllOrderDetails().stream()
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Đếm tổng số lượng sản phẩm đã bán
    public Integer getTotalProductsSold() {
        return getAllOrderDetails().stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }
}


