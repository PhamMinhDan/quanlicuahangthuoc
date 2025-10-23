package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Payment;
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
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT p FROM Payment p JOIN FETCH p.order o JOIN FETCH o.customer " +
            "WHERE (:orderId IS NULL OR o.id = :orderId) " +
            "AND (:paymentDate IS NULL OR p.paymentDate = :paymentDate)")
    Page<Payment> findByFilters(@Param("orderId") Integer orderId,
                                @Param("paymentDate") LocalDate paymentDate,
                                Pageable pageable);

    @Query("SELECT p FROM Payment p JOIN FETCH p.order o JOIN FETCH o.customer WHERE p.id = :id")
    Optional<Payment> findByIdWithOrderAndCustomer(@Param("id") Integer id);

    @Query("SELECT COUNT(p) FROM Payment p")
    long countPayments();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p")
    Double sumTotalAmount();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = 'tien_mat'")
    long countCashPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = 'chuyen_khoan'")
    long countTransferPayments();
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    long countByPaymentMethodAndPaymentDateBetween(Payment.PaymentMethod paymentMethod, LocalDate startDate, LocalDate endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m'), COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p " +
            "WHERE p.paymentDate >= :startDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m')")
    List<Object[]> findRevenueByMonth(@Param("startDate") LocalDate startDate);

}