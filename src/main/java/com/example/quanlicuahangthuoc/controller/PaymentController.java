package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Payment;
import com.example.quanlicuahangthuoc.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    // ✅ Bọc trong transaction để tránh lỗi LazyInitializationException
    @Transactional
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
