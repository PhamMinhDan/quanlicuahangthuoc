package com.example.quanlicuahangthuoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quanlicuahangthuoc.service.OrderService;


@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Xử lý xóa đơn hàng (khi bấm nút Xóa)
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        boolean deleted = orderService.deleteOrder(id);

        // Sau khi xóa xong thì quay lại danh sách
        return "redirect:/orders/list";
    }
}