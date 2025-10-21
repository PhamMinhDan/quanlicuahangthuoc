package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    // Hiển thị trang danh sách order details
    @GetMapping("/view-order-details")
    public String getOrderDetailsPage(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "medicineName", required = false) String medicineName,
            @RequestParam(value = "customerName", required = false) String customerName,
            Model model) {

        List<OrderDetail> orderDetails;

        // Nếu có tìm kiếm, dùng search
        if (orderId != null || (medicineName != null && !medicineName.isEmpty())
                || (customerName != null && !customerName.isEmpty())) {
            orderDetails = orderDetailService.searchOrderDetails(orderId, medicineName, customerName);
        } else {
            // Nếu không có tìm kiếm, lấy tất cả
            orderDetails = orderDetailService.getAllOrderDetails();
        }

        // Tính toán các thống kê
        BigDecimal totalRevenue = orderDetailService.getTotalRevenue();
        Integer totalProductsSold = orderDetailService.getTotalProductsSold();
        int totalOrders = (int) orderDetails.stream()
                .map(od -> od.getOrder().getId())
                .distinct()
                .count();

        // Thêm vào model
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProductsSold", totalProductsSold);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalItems", orderDetails.size());

        // Thêm các tham số search vào model để giữ lại trong form
        model.addAttribute("orderId", orderId);
        model.addAttribute("medicineName", medicineName);
        model.addAttribute("customerName", customerName);

        model.addAttribute("activeNav", "order-details");

        return "order-detail";
    }

    // REST API - Lấy tất cả order details (trả về Map để tránh circular reference)
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllOrderDetails() {
        List<OrderDetail> orderDetails = orderDetailService.getAllOrderDetails();

        List<Map<String, Object>> response = orderDetails.stream().map(od -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", od.getId());
            map.put("orderId", od.getOrder() != null ? od.getOrder().getId() : null);
            map.put("customerName", od.getOrder() != null && od.getOrder().getCustomer() != null
                    ? od.getOrder().getCustomer().getName() : null);
            map.put("medicineId", od.getMedicine() != null ? od.getMedicine().getId() : null);
            map.put("medicineName", od.getMedicine() != null ? od.getMedicine().getName() : null);
            map.put("quantity", od.getQuantity());
            map.put("unitPrice", od.getUnitPrice());
            map.put("totalPrice", od.getTotalPrice());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // REST API - Lấy order detail theo ID (trả về Map)
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderDetailById(@PathVariable Integer id) {
        return orderDetailService.getOrderDetailById(id)
                .map(od -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", od.getId());
                    map.put("orderId", od.getOrder() != null ? od.getOrder().getId() : null);
                    map.put("customerName", od.getOrder() != null && od.getOrder().getCustomer() != null
                            ? od.getOrder().getCustomer().getName() : null);
                    map.put("medicineId", od.getMedicine() != null ? od.getMedicine().getId() : null);
                    map.put("medicineName", od.getMedicine() != null ? od.getMedicine().getName() : null);
                    map.put("quantity", od.getQuantity());
                    map.put("unitPrice", od.getUnitPrice());
                    map.put("totalPrice", od.getTotalPrice());
                    return ResponseEntity.ok(map);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // REST API - Lấy order details theo order ID (trả về Map)
    @GetMapping("/by-order/{orderId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);

        List<Map<String, Object>> response = orderDetails.stream().map(od -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", od.getId());
            map.put("orderId", od.getOrder() != null ? od.getOrder().getId() : null);
            map.put("customerName", od.getOrder() != null && od.getOrder().getCustomer() != null
                    ? od.getOrder().getCustomer().getName() : null);
            map.put("medicineId", od.getMedicine() != null ? od.getMedicine().getId() : null);
            map.put("medicineName", od.getMedicine() != null ? od.getMedicine().getName() : null);
            map.put("quantity", od.getQuantity());
            map.put("unitPrice", od.getUnitPrice());
            map.put("totalPrice", od.getTotalPrice());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // TEST endpoint - Đếm số lượng records
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<String> countOrderDetails() {
        try {
            long count = orderDetailService.getAllOrderDetails().size();
            return ResponseEntity.ok("Tổng số OrderDetail: " + count);
        } catch (Exception e) {
            return ResponseEntity.ok("Lỗi: " + e.getMessage());
        }
    }
}
