package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.*;
import com.example.quanlicuahangthuoc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MedicineService medicineService;

    // 🧾 Form thêm mới
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("orderDetail", new OrderDetail());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "order-detail/add"; // => src/main/resources/templates/order-detail/add.html
    }

    // 💾 Submit form thêm mới
    @PostMapping("/new")
    public String addOrderDetail(@RequestParam Integer orderId,
                                 @RequestParam Integer medicineId,
                                 @RequestParam Integer quantity,
                                 @RequestParam BigDecimal unitPrice,
                                 RedirectAttributes redirectAttributes) {
        try {
            orderDetailService.addOrderDetail(orderId, medicineId, quantity, unitPrice);
            redirectAttributes.addFlashAttribute("message", "Thêm chi tiết đơn hàng thành công!");
            return "redirect:/order-details/new";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order-details/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm chi tiết đơn hàng: " + e.getMessage());
            return "redirect:/order-details/new";
        }
    }
}
