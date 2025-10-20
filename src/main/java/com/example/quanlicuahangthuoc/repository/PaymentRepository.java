package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}