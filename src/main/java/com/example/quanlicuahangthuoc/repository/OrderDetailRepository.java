package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    // Phương thức custom: Tìm tất cả chi tiết đơn hàng theo ID của đơn hàng
    List<OrderDetail> findByOrderId(Integer orderId);
}