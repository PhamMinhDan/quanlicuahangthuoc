package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    public List<OrderDetail> searchOrderDetails(Integer orderId, Integer medicineId) {
        // Ưu tiên tìm kiếm kết hợp nếu cả hai tiêu chí được cung cấp
        if (orderId != null && medicineId != null) {
            return orderDetailRepository.findByOrderIdOrMedicineId(orderId, medicineId);
        } else if (orderId != null) {
            // Chỉ tìm theo Mã đơn
            return orderDetailRepository.findByOrderId(orderId);
        } else if (medicineId != null) {
            // Chỉ tìm theo Mã thuốc
            return orderDetailRepository.findByMedicineId(medicineId);
        } else {
            // Không có tiêu chí tìm kiếm nào
            return List.of();
        }
    }

    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }
}