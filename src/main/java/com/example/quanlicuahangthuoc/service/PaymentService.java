package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Payment;
import com.example.quanlicuahangthuoc.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    /**
     * Lấy danh sách thanh toán có sắp xếp.
     * @param sortField  Trường cần sắp xếp (ví dụ: "paymentDate" hoặc "amount")
     * @param sortDir    Chiều sắp xếp ("asc" hoặc "desc")
     */
    @Transactional
    public List<Payment> getAllPaymentsSorted(String sortField, String sortDir) {
        try {
            // Tạo đối tượng Sort theo trường và chiều được chọn
            org.springframework.data.domain.Sort sort =
                    sortDir.equalsIgnoreCase("asc")
                            ? org.springframework.data.domain.Sort.by(sortField).ascending()
                            : org.springframework.data.domain.Sort.by(sortField).descending();

            // Giả sử repository có sẵn phương thức findAll(Sort sort)
            return paymentRepository.findAll(sort);
        } catch (Exception e) {
            System.err.println("Error sorting payments: " + e.getMessage());
            return List.of();
        }
    }
    public Page<Payment> getPaymentsPaged(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return paymentRepository.findAll(pageable);
    }


}
