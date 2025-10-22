package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping
    public String listPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Model model) {
        
        List<Promotion> allPromotions;
        
        Page<Promotion> promotionPage;
        if (sortBy != null && !sortBy.isEmpty()) {
            promotionPage = promotionService.getAllPromotionsPaginatedAndSorted(page, size, sortBy, sortOrder);
        } else {
            promotionPage = promotionService.getAllPromotionsPaginated(page, size);
        }
        
        allPromotions = promotionPage.getContent();
        model.addAttribute("promotions", allPromotions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promotionPage.getTotalPages());
        model.addAttribute("totalItems", promotionPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        List<Promotion> allPromotionsForStats = promotionService.getAllPromotions();
        long activeCount = allPromotionsForStats.stream()
                .filter(p -> p.getExpiredDate() != null && p.getExpiredDate().isAfter(java.time.LocalDate.now()))
                .count();
        long expiredCount = allPromotionsForStats.stream()
                .filter(p -> p.getExpiredDate() != null && (p.getExpiredDate().isBefore(java.time.LocalDate.now()) || p.getExpiredDate().isEqual(java.time.LocalDate.now())))
                .count();
        
        model.addAttribute("totalPromotions", allPromotionsForStats.size());
        model.addAttribute("activePromotions", activeCount);
        model.addAttribute("expiredPromotions", expiredCount);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("activeNav", "promotions");
        
        return "promotion/list";
    }
    

    @GetMapping("/{id}")
    public String viewPromotion(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            model.addAttribute("activeNav", "promotions");
            return "promotion/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotions";
        }
    }
    
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
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            model.addAttribute("activeNav", "promotions");
            return "promotion/edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotions";
        }
    }
    
     @PostMapping("/update/{id}")
    public String updatePromotion(
            @PathVariable Integer id,
            @Valid @ModelAttribute("promotion") Promotion promotion,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            return "promotion/edit";
        }
        
        try {
            promotionService.updatePromotion(id, promotion);
            redirectAttributes.addFlashAttribute("message", "Cập nhật khuyến mãi thành công!");
            return "redirect:/promotions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "promotion/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật khuyến mãi: " + e.getMessage());
            return "promotion/edit";
        }
    }
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("activeNav", "promotions");
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
    
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false, defaultValue = "") String searchName,
            @RequestParam(required = false, defaultValue = "") String searchType,
            Model model) {
        
        List<Promotion> promotions;
        boolean hasName = searchName != null && !searchName.trim().isEmpty();
        boolean hasType = searchType != null && !searchType.trim().isEmpty();
        
        if (!hasName && !hasType) {
            promotions = promotionService.getAllPromotions();
        } else if (hasName && hasType) {
            promotions = promotionService.searchByNameAndType(searchName.trim(), searchType.trim());
        } else if (hasName) {
            promotions = promotionService.searchByName(searchName.trim());
        } else {
            promotions = promotionService.searchByType(searchType.trim());
        }
        
        long activeCount = promotions.stream()
                .filter(p -> p.getExpiredDate() != null && p.getExpiredDate().isAfter(java.time.LocalDate.now()))
                .count();
        long expiredCount = promotions.stream()
                .filter(p -> p.getExpiredDate() != null && (p.getExpiredDate().isBefore(java.time.LocalDate.now()) || p.getExpiredDate().isEqual(java.time.LocalDate.now())))
                .count();
        
        model.addAttribute("promotions", promotions);
        model.addAttribute("totalPromotions", promotions.size());
        model.addAttribute("activePromotions", activeCount);
        model.addAttribute("expiredPromotions", expiredCount);
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchType", searchType);
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }
    
    @GetMapping("/search/name")
    public String searchByName(@RequestParam String name, Model model) {
        List<Promotion> promotions = promotionService.searchByName(name);
        
        long activeCount = promotions.stream()
                .filter(p -> p.getExpiredDate() != null && p.getExpiredDate().isAfter(java.time.LocalDate.now()))
                .count();
        long expiredCount = promotions.stream()
                .filter(p -> p.getExpiredDate() != null && (p.getExpiredDate().isBefore(java.time.LocalDate.now()) || p.getExpiredDate().isEqual(java.time.LocalDate.now())))
                .count();
        
        model.addAttribute("promotions", promotions);
        model.addAttribute("totalPromotions", promotions.size());
        model.addAttribute("activePromotions", activeCount);
        model.addAttribute("expiredPromotions", expiredCount);
        model.addAttribute("searchType", "name");
        model.addAttribute("searchValue", name);
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }
    
    @GetMapping("/search/type")
    public String searchByType(@RequestParam String type, Model model) {
        List<Promotion> promotions = promotionService.searchByType(type);
        
        long activeCount = promotions.stream()
                .filter(p -> p.getExpiredDate() != null && p.getExpiredDate().isAfter(java.time.LocalDate.now()))
                .count();
        long expiredCount = promotions.stream()
                .filter(p -> p.getExpiredDate() != null && (p.getExpiredDate().isBefore(java.time.LocalDate.now()) || p.getExpiredDate().isEqual(java.time.LocalDate.now())))
                .count();
        
        model.addAttribute("promotions", promotions);
        model.addAttribute("totalPromotions", promotions.size());
        model.addAttribute("activePromotions", activeCount);
        model.addAttribute("expiredPromotions", expiredCount);
        model.addAttribute("searchType", "type");
        model.addAttribute("searchValue", type);
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }
    
    @GetMapping("/search/name-and-type")
    public String searchByNameAndType(
            @RequestParam String name, 
            @RequestParam String type,
            Model model) {
        List<Promotion> promotions = promotionService.searchByNameAndType(name, type);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name-and-type");
        model.addAttribute("searchName", name);
        model.addAttribute("searchTypeValue", type);
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }
    
    @GetMapping("/search/name-or-type")
    public String searchByNameOrType(
            @RequestParam String name, 
            @RequestParam String type,
            Model model) {
        List<Promotion> promotions = promotionService.searchByNameOrType(name, type);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name-or-type");
        model.addAttribute("searchName", name);
        model.addAttribute("searchTypeValue", type);
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }
}
