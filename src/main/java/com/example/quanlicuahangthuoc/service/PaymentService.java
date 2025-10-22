package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Payment;
import com.example.quanlicuahangthuoc.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Lấy danh sách tất cả thanh toán từ database.
     */
    @Transactional
    public List<Payment> getAllPayments() {
        try {
            return paymentRepository.findAllWithOrderAndCustomer();
        } catch (Exception e) {
            System.err.println("Error fetching payments: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy thông tin thanh toán theo ID.
     */
    @Transactional
    public Payment getPaymentById(Integer id) {
        try {
            return paymentRepository.findByIdWithOrderAndCustomer(id);
        } catch (Exception e) {
            System.err.println("Error fetching payment by id: " + e.getMessage());
            return null;
        }
    }
}
