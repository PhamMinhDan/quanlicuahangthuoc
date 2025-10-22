package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        return "promotion/add";
    }
    
    @PostMapping
    public String addPromotion(
            @Valid @ModelAttribute("promotion") Promotion promotion,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            return "promotion/add";
        }
        
        try {
            promotionService.addPromotion(promotion);
            redirectAttributes.addFlashAttribute("message", "Thêm khuyến mãi thành công!");
            return "redirect:/promotions/new";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "promotion/add";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm khuyến mãi: " + e.getMessage());
            return "promotion/add";
        }
    }
}
