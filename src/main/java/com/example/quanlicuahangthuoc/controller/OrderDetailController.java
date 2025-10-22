package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public String viewOrderDetails(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "medicineName", required = false) String medicineName,
            @RequestParam(value = "customerName", required = false) String customerName,
            Model model) {

        List<OrderDetail> orderDetails;

        if (orderId != null || (medicineName != null && !medicineName.isEmpty())
                || (customerName != null && !customerName.isEmpty())) {
            orderDetails = orderDetailService.searchOrderDetails(orderId, medicineName, customerName);
        } else {
            orderDetails = orderDetailService.getAllOrderDetails();
        }

        BigDecimal totalRevenue = orderDetailService.getTotalRevenue();
        Integer totalProductsSold = orderDetailService.getTotalProductsSold();
        int totalOrders = (int) orderDetails.stream()
                .map(od -> od.getOrder().getId())
                .distinct()
                .count();

        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProductsSold", totalProductsSold);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalItems", orderDetails.size());

        model.addAttribute("orderId", orderId);
        model.addAttribute("medicineName", medicineName);
        model.addAttribute("customerName", customerName);

        model.addAttribute("activeNav", "order-details");

        return "order-detail";
    }

    @GetMapping("/{id}")
    public String viewOrderDetailById(@PathVariable Integer id, Model model) {
        Optional<OrderDetail> orderDetailOpt = orderDetailService.getOrderDetailById(id);

        if (orderDetailOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy chi tiết đơn hàng với ID: " + id);
            return "error";
        }

        OrderDetail orderDetail = orderDetailOpt.get();

        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("orderId", orderDetail.getOrder() != null ? orderDetail.getOrder().getId() : null);
        model.addAttribute("customerName", orderDetail.getOrder() != null && orderDetail.getOrder().getCustomer() != null
                ? orderDetail.getOrder().getCustomer().getName() : null);
        model.addAttribute("medicineName", orderDetail.getMedicine() != null ? orderDetail.getMedicine().getName() : null);

        return "order-detail-info";
    }

    @GetMapping("/count")
    @ResponseBody
    public String countOrderDetails() {
        try {
            long count = orderDetailService.getAllOrderDetails().size();
            return "Tổng số OrderDetail: " + count;
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }
}
