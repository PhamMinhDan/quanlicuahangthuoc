package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    // Tìm kiếm order details theo orderId, tên thuốc, tên khách hàng
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

    // Chuyển một OrderDetail thành Map để dùng cho API JSON
    public Map<String, Object> convertToMap(OrderDetail od) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", od.getId());
        map.put("orderId", od.getOrder() != null ? od.getOrder().getId() : null);
        map.put("customerName", od.getOrder() != null && od.getOrder().getCustomer() != null
                ? od.getOrder().getCustomer().getName() : null);
        map.put("medicineId", od.getMedicine() != null ? od.getMedicine().getId() : null);
        map.put("medicineName", od.getMedicine() != null ? od.getMedicine().getName() : null);
        map.put("quantity", od.getQuantity());
        map.put("unitPrice", od.getUnitPrice());
        map.put("totalPrice", od.getTotalPrice());
        return map;
    }

    // Chuyển danh sách OrderDetail thành danh sách Map
    public List<Map<String, Object>> convertToMapList(List<OrderDetail> list) {
        return list.stream().map(this::convertToMap).collect(Collectors.toList());
    }
}
