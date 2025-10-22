package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    /**
     * Lấy danh sách chi tiết đơn hàng theo ID của đơn hàng cha.
     */
    public List<OrderDetail> getDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    /**
     * Xóa một chi tiết đơn hàng khỏi cơ sở dữ liệu dựa trên ID.
     * @param id ID của OrderDetail cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy ID
     */
    public boolean deleteOrderDetail(Integer id) {
        if (orderDetailRepository.existsById(id)) {
            // Xóa chi tiết đơn hàng
            orderDetailRepository.deleteById(id);
            // Bản ghi bị xóa, không còn hiển thị trong danh sách khi truy vấn lại.
            return true;
        }
        // ID không tồn tại
        return false;
    }
}