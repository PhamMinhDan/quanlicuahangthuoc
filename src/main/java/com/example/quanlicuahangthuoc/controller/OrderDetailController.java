package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * Hiển thị danh sách chi tiết đơn hàng có phân trang (10 bản ghi / trang)
     */
    @GetMapping
    public String listOrderDetails(
            @RequestParam(value = "page", defaultValue = "0") int page,         // Trang hiện tại
            @RequestParam(value = "size", defaultValue = "10") int size,        // Số bản ghi mỗi trang
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, // Trường sắp xếp
            @RequestParam(value = "direction", defaultValue = "asc") String direction, // Thứ tự sắp xếp
            Model model) {

        // Lấy danh sách phân trang
        Page<OrderDetail> orderDetailPage = orderDetailService.getPagedOrderDetails(page, size, sortBy, direction);

        // Tính toán thống kê (có thể tuỳ chỉnh)
        BigDecimal totalRevenue = orderDetailService.getTotalRevenue();
        Integer totalProductsSold = orderDetailService.getTotalProductsSold();
        int totalOrders = (int) orderDetailPage.getContent().stream()
                .map(od -> od.getOrder().getId())
                .distinct()
                .count();

        // Truyền dữ liệu sang View
        model.addAttribute("orderDetailsPage", orderDetailPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderDetailPage.getTotalPages());
        model.addAttribute("totalItems", orderDetailPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        // Thống kê
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProductsSold", totalProductsSold);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("activeNav", "order-details");

        // Trả về giao diện
        return "order-detail";
    }
}
