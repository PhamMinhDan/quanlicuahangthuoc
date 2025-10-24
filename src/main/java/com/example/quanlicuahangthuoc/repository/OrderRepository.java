package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByCustomerId(Integer customerId);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.customer " +
           "LEFT JOIN FETCH o.staff " +
           "LEFT JOIN FETCH o.promotion " +
           "LEFT JOIN FETCH o.orderDetails od " +
           "LEFT JOIN FETCH od.medicine " +
           "LEFT JOIN FETCH od.staff " +
           "LEFT JOIN FETCH od.promotion " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT o FROM Order o " +
            "WHERE (:customerId IS NULL OR o.customer.id = :customerId) " +
            "AND (:orderDate IS NULL OR o.orderDate = :orderDate) " +
            "AND (:status IS NULL OR o.status = :status)")
    Page<Order> findOrdersDynamically(
            @Param("customerId") Integer customerId,
            @Param("orderDate") LocalDate orderDate,
            @Param("status") Order.OrderStatus status,
            Pageable pageable);
    
    @Query("SELECT o FROM Order o " +
            "WHERE (:customerId IS NULL OR o.customer.id = :customerId) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND ((:fromDate IS NULL AND :toDate IS NULL) OR " +
            "     (:fromDate IS NOT NULL AND :toDate IS NULL AND o.orderDate = :fromDate) OR " +
            "     (:fromDate IS NULL AND :toDate IS NOT NULL AND o.orderDate = :toDate) OR " +
            "     (:fromDate IS NOT NULL AND :toDate IS NOT NULL AND o.orderDate BETWEEN :fromDate AND :toDate))")
    Page<Order> findOrdersByDateRange(
            @Param("customerId") Integer customerId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") Order.OrderStatus status,
            Pageable pageable);
    @Query("SELECT COUNT(o) FROM Order o")
    long countOrders();
    @Query("SELECT FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m'), COUNT(DISTINCT o.customer.id) " +
            "FROM Order o " +
            "WHERE o.orderDate >= :startDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m')")
    List<Object[]> countCustomersByMonth(@Param("startDate") LocalDate startDate);

    long countByStatusAndOrderDateBetween(Order.OrderStatus status, LocalDate startDate, LocalDate endDate);

    long countByOrderDateBetween(LocalDate startDate, LocalDate endDate);
}