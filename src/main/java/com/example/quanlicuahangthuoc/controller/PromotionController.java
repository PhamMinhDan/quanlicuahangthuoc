package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
  
    @GetMapping
    public String listPromotions(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Model model) {
        
        if (page != null) {
     
            Page<Promotion> promotionPage;
            if (sortBy != null) {
                promotionPage = promotionService.getAllPromotionsPaginatedAndSorted(page, size, sortBy, sortOrder);
            } else {
                promotionPage = promotionService.getAllPromotionsPaginated(page, size);
            }
            
            model.addAttribute("promotions", promotionPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", promotionPage.getTotalPages());
            model.addAttribute("totalItems", promotionPage.getTotalElements());
            model.addAttribute("pageSize", size);
        } else {
       
            model.addAttribute("promotions", promotionService.getAllPromotions());
        }
        
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        
        return "promotion/list";
    }
    
  
    @GetMapping("/{id}")
    public String viewPromotion(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Promotion> promotion = promotionService.getPromotionById(id);
        
        if (promotion.isPresent()) {
            model.addAttribute("promotion", promotion.get());
            return "promotion/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi!");
            return "redirect:/promotions";
        }
    }
}