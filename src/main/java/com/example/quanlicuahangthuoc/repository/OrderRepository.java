package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByCustomerId(Integer customerId);

    @Query("SELECT o FROM Order o " +
            "WHERE (:customerId IS NULL OR o.customer.id = :customerId) " +
            "AND (:orderDate IS NULL OR o.orderDate = :orderDate) " +
            "AND (:status IS NULL OR o.status = :status)")
    Page<Order> findOrdersDynamically(
            @Param("customerId") Integer customerId,
            @Param("orderDate") LocalDate orderDate,
            @Param("status") Order.OrderStatus status,
            Pageable pageable);
    @Query("SELECT COUNT(o) FROM Order o")
    long countOrders();
}