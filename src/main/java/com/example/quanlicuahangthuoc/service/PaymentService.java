package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Payment;
import com.example.quanlicuahangthuoc.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Parse date từ string dd/MM/yyyy
    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    public DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
    public Page<Payment> getPaymentsPaged(Integer orderId, LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return paymentRepository.findByDateRange(orderId, fromDate, toDate, pageable);
    }

    @Transactional
    public Payment getPaymentById(Integer id) {
        return paymentRepository.findByIdWithOrderAndCustomer(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy thanh toán với ID: " + id));
    }

    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    @Transactional
    public void updatePayment(Integer id, Payment updatedPayment) {
        Payment existingPayment = getPaymentById(id);
        existingPayment.setOrder(updatedPayment.getOrder());
        existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setChange(updatedPayment.getChange());
        existingPayment.setPaymentDate(updatedPayment.getPaymentDate());
        paymentRepository.save(existingPayment);
    }

    @Transactional
    public void deletePayment(Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy thanh toán với ID: " + id);
        }
        paymentRepository.deleteById(id);
    }

    public long getTotalPayments() {
        return paymentRepository.countPayments();
    }

    public Double getTotalAmount() {
        return paymentRepository.sumTotalAmount();
    }

    public long getTotalCashPayments() {
        return paymentRepository.countCashPayments();
    }

    public long getTotalTransferPayments() {
        return paymentRepository.countTransferPayments();
    }

    @Transactional(readOnly = true)
    public Double getTotalAmountInPeriod(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(payment -> payment.getAmount() != null ? payment.getAmount().doubleValue() : 0.0)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Transactional(readOnly = true)
    public long getTotalCashPaymentsInPeriod(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.countByPaymentMethodAndPaymentDateBetween(Payment.PaymentMethod.tien_mat, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public long getTotalTransferPaymentsInPeriod(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.countByPaymentMethodAndPaymentDateBetween(Payment.PaymentMethod.chuyen_khoan, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getRevenueByMonth(LocalDate startDate) {
        List<Object[]> results = paymentRepository.findRevenueByMonth(startDate);
        Map<String, Double> revenueByMonth = new HashMap<>();
        for (Object[] result : results) {
            String month = (String) result[0];
            Double amount = ((Number) result[1]).doubleValue();
            revenueByMonth.put(month, amount);
        }
        return revenueByMonth;
    }

}