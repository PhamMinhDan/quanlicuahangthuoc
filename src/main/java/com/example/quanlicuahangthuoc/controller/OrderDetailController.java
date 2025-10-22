package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.OrderDetail;
import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.Optional;

@Controller
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<OrderDetail> detailOptional = orderDetailService.findById(id);

        if (detailOptional.isPresent()) {
            // Đặt đối tượng vào Model để form hiển thị dữ liệu hiện tại
            model.addAttribute("orderDetail", detailOptional.get());
            // Trả về tên View (order_detail_edit.html)
            return "order_detail_edit";
        } else {
            // Không tìm thấy
            redirectAttributes.addFlashAttribute("errorMessage", "Chi tiết đơn hàng không tồn tại.");
            return "redirect:/orders/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateOrderDetail(
            @PathVariable Integer id,
            @Valid @ModelAttribute("orderDetail") OrderDetail updatedDetail,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra lỗi Validation
        if (result.hasErrors()) {
            // Nếu có lỗi, hiển thị lại form
            return "order_detail_edit";
        }

        // 2. Thực hiện cập nhật
        OrderDetail updated = orderDetailService.updateOrderDetail(id, updatedDetail);

        if (updated != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chi tiết đơn hàng thành công!");

            // 3. Lớp bảo vệ khi chuyển hướng: Đảm bảo Order tồn tại
            if (updated.getOrder() != null) {
                // Chuyển hướng về trang chi tiết của đơn hàng chứa chi tiết này
                return "redirect:/orders/" + updated.getOrder().getId();
            } else {
                // Nếu Order ID không tồn tại (trường hợp hiếm), chuyển về trang danh sách
                redirectAttributes.addFlashAttribute("warningMessage", "Chi tiết đơn hàng đã được cập nhật, nhưng không thể tìm thấy đơn hàng chính.");
                return "redirect:/orders/list";
            }
        } else {
            // Lỗi không tìm thấy đối tượng
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chi tiết đơn hàng để cập nhật.");
            return "redirect:/orders/list";
        }
    }
}