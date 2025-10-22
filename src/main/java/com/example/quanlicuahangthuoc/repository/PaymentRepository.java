package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT p FROM Payment p JOIN FETCH p.order o JOIN FETCH o.customer")
    List<Payment> findAllWithOrderAndCustomer();

    @Query("SELECT p FROM Payment p JOIN FETCH p.order o JOIN FETCH o.customer WHERE p.id = :id")
    Payment findByIdWithOrderAndCustomer(@Param("id") Integer id);

    @Query("SELECT p FROM Payment p WHERE " +
            "LOWER(p.paymentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Payment> findByKeyword(@Param("keyword") String keyword);

}