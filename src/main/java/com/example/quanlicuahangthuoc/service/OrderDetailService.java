package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.*;
import com.example.quanlicuahangthuoc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Transactional
    public OrderDetail addOrderDetail(Integer orderId, Integer medicineId, Integer quantity, BigDecimal unitPrice) {
        // 1️⃣ Kiểm tra rỗng
        if (orderId == null || medicineId == null || quantity == null || unitPrice == null) {
            throw new IllegalArgumentException("Các trường không được để trống!");
        }

        // 2️⃣ Kiểm tra Order và Medicine tồn tại
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Mã đơn hàng không hợp lệ!"));
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Mã thuốc không hợp lệ!"));

        // 3️⃣ Kiểm tra số lượng và đơn giá
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn hoặc bằng 0!");
        }

        // 4️⃣ Tạo mới OrderDetail
        OrderDetail orderDetail = new OrderDetail(order, medicine, quantity, unitPrice);
        return orderDetailRepository.save(orderDetail);
    }
}
