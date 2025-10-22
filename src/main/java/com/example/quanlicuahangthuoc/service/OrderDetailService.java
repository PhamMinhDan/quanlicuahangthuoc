package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    /**
     * Cập nhật thông tin chi tiết đơn hàng (Quantity và UnitPrice).
     * * @param id ID của chi tiết đơn hàng cần cập nhật.
     * @param updatedDetail Dữ liệu mới (đã được kiểm tra hợp lệ từ Controller).
     * @return OrderDetail đã được cập nhật, hoặc null nếu không tìm thấy.
     */
    public OrderDetail updateOrderDetail(Integer id, OrderDetail updatedDetail) {
        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findById(id);

        if (existingDetailOpt.isPresent()) {
            OrderDetail existingDetail = existingDetailOpt.get();

            // Cập nhật các trường có thể thay đổi: Số lượng và Đơn giá
            existingDetail.setQuantity(updatedDetail.getQuantity());
            existingDetail.setUnitPrice(updatedDetail.getUnitPrice());

            // Lưu vào DB
            return orderDetailRepository.save(existingDetail);
        } else {
            // Không tìm thấy
            return null;
        }
    }

    public Optional<OrderDetail> findById(Integer id) {
        return orderDetailRepository.findById(id);
    }
}