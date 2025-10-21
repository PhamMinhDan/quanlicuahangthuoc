package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Tìm tất cả order details theo order ID
    List<OrderDetail> findByOrderId(Integer orderId);

    // Tìm tất cả order details theo medicine ID
    List<OrderDetail> findByMedicineId(Integer medicineId);

    // Query với pagination và eager fetch để tránh N+1 problem
    @Query("SELECT od FROM OrderDetail od " +
            "LEFT JOIN FETCH od.order o " +
            "LEFT JOIN FETCH od.medicine m " +
            "LEFT JOIN FETCH o.customer")
    List<OrderDetail> findAllWithDetails();

    // Search order details
    @Query("SELECT od FROM OrderDetail od " +
            "LEFT JOIN FETCH od.order o " +
            "LEFT JOIN FETCH od.medicine m " +
            "LEFT JOIN FETCH o.customer c " +
            "WHERE (:orderId IS NULL OR o.id = :orderId) " +
            "AND (:medicineName IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :medicineName, '%'))) " +
            "AND (:customerName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :customerName, '%')))")
    List<OrderDetail> searchOrderDetails(@Param("orderId") Integer orderId,
                                         @Param("medicineName") String medicineName,
                                         @Param("customerName") String customerName);
}
