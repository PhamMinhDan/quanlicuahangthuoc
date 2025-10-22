package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean deleted = promotionService.deletePromotion(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Đã xóa khuyến mãi có ID: " + id);
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi có ID: " + id);
        }
        return "redirect:/promotions";
    }
}
