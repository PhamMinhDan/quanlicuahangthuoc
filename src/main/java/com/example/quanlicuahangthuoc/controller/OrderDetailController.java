package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public String getOrderDetailsPage(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "medicineName", required = false) String medicineName,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction,
            Model model) {

        List<OrderDetail> orderDetails;
        if (orderId != null || (medicineName != null && !medicineName.isEmpty())
                || (customerName != null && !customerName.isEmpty())) {
            orderDetails = orderDetailService.searchOrderDetails(orderId, medicineName, customerName);
        } else {
            orderDetails = orderDetailService.getAllOrderDetails();
        }

        Comparator<OrderDetail> comparator;
        switch (sortBy) {
            case "orderId":
                comparator = Comparator.comparing(od -> od.getOrder().getId());
                break;
            case "totalPrice":
                comparator = Comparator.comparing(OrderDetail::getTotalPrice);
                break;
            default:
                comparator = Comparator.comparing(OrderDetail::getId);
        }
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        orderDetails.sort(comparator);

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
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("activeNav", "order-details");

        return "order-detail";
    }
}
