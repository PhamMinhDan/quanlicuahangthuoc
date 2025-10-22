package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Tìm kiếm theo order.id
    List<OrderDetail> findByOrderId(Integer orderId);

    // Tìm kiếm theo medicine.id
    List<OrderDetail> findByMedicineId(Integer medicineId);

    // Tìm kiếm theo order.id HOẶC medicine.id
    List<OrderDetail> findByOrderIdOrMedicineId(Integer orderId, Integer medicineId);
}