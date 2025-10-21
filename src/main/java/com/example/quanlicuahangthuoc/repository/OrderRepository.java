package com.example.quanlicuahangthuoc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quanlicuahangthuoc.entity.Order;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAll();
    Page<Order> findAll(Pageable pageable);
    List<Order> findByOrderId(Integer id);
    Page<Order> findByOrderId(Integer id, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (:customerId IS NULL OR o.customer.id = :customerId) "
            + "AND (:orderDate IS NULL OR o.orderDate = :orderDate) "
            + "AND (:status IS NULL OR o.status = :status)")
    Page<Order> searchOrders(@Param("customerId") Integer customerId,
                             @Param("orderDate") java.time.LocalDate orderDate,
                             @Param("status") Order.OrderStatus status,
                             Pageable pageable);
}
