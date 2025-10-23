package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.service.CustomerService;
import com.example.quanlicuahangthuoc.service.OrderService;
import com.example.quanlicuahangthuoc.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String showDashboard(Model model) {
        try {
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            LocalDate sixMonthsAgo = now.minusMonths(6).withDayOfMonth(1);

            // Thống kê tháng hiện tại
            model.addAttribute("totalRevenueThisMonth", paymentService.getTotalAmountInPeriod(startOfMonth, endOfMonth));
            model.addAttribute("totalOrdersThisMonth", orderService.countOrdersInPeriod(startOfMonth, endOfMonth));
            model.addAttribute("totalCustomersThisMonth", customerService.getCustomersThisMonth());
            model.addAttribute("totalTransferPaymentsThisMonth", paymentService.getTotalTransferPaymentsInPeriod(startOfMonth, endOfMonth));

            // Doanh thu theo tháng
            List<String> revenueMonths = new ArrayList<>();
            List<Double> revenueData = new ArrayList<>();
            Map<String, Double> revenueByMonth = paymentService.getRevenueByMonth(sixMonthsAgo);
            for (int i = 0; i < 6; i++) {
                LocalDate month = now.minusMonths(5 - i);
                String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                revenueMonths.add(month.getMonthValue() + "/" + month.getYear());
                revenueData.add(revenueByMonth.getOrDefault(monthKey, 0.0));
            }
            model.addAttribute("revenueMonths", revenueMonths);
            model.addAttribute("revenueData", revenueData);

            // Phương thức thanh toán
            long cashPayments = paymentService.getTotalCashPaymentsInPeriod(startOfMonth, endOfMonth);
            long transferPayments = paymentService.getTotalTransferPaymentsInPeriod(startOfMonth, endOfMonth);
            model.addAttribute("paymentMethodLabels", List.of("Tiền Mặt", "Chuyển Khoản"));
            model.addAttribute("paymentMethodData", List.of(cashPayments, transferPayments));

            // Trạng thái đơn hàng
            Map<Order.OrderStatus, Long> orderStatusCount = orderService.getOrderStatusCount(startOfMonth, endOfMonth);
            List<String> orderStatusLabels = new ArrayList<>();
            List<Long> orderStatusData = new ArrayList<>();
            for (Order.OrderStatus status : Order.OrderStatus.values()) {
                orderStatusLabels.add(status.getDisplayName());
                orderStatusData.add(orderStatusCount.getOrDefault(status, 0L));
            }
            model.addAttribute("orderStatusLabels", orderStatusLabels);
            model.addAttribute("orderStatusData", orderStatusData);

            // Khách hàng theo tháng
            List<String> customerMonths = new ArrayList<>();
            List<Long> customerData = new ArrayList<>();
            Map<String, Long> customersByMonth = orderService.getCustomersByMonth(sixMonthsAgo);
            for (int i = 0; i < 6; i++) {
                LocalDate month = now.minusMonths(5 - i);
                String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                customerMonths.add(month.getMonthValue() + "/" + month.getYear());
                customerData.add(customersByMonth.getOrDefault(monthKey, 0L));
            }
            model.addAttribute("customerMonths", customerMonths);
            model.addAttribute("customerData", customerData);

            model.addAttribute("activeNav", "dashboard");
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dashboard: " + e.getMessage());
            return "dashboard";
        }
    }
}