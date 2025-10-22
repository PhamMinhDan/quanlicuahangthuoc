package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Import đối tượng Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) Integer medicineId,
            Model model) { // Thêm Model để truyền dữ liệu

        List<OrderDetail> results = orderDetailService.searchOrderDetails(orderId, medicineId);

        // 1. Gắn danh sách kết quả vào Model
        model.addAttribute("orderDetails", results);

        // 2. Gắn các tiêu chí tìm kiếm vào Model (để giữ lại trên form)
        model.addAttribute("searchOrderId", orderId);
        model.addAttribute("searchMedicineId", medicineId);

        // 3. Trả về tên của View (ví dụ: file order-detail-list.html)
        return "order-detail-list";
    }
}