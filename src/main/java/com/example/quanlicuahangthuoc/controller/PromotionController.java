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

    // ✅ Danh sách có phân trang
    @GetMapping
    public String listPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Model model) {

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
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "promotion/list";
    }

    // ✅ Xem chi tiết
    @GetMapping("/{id}")
    public String viewPromotion(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            return "promotion/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotions";
        }
    }

    // ✅ Xóa
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

    // ✅ Sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
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

    // ✅ Thêm mới
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
            return "redirect:/promotions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "promotion/add";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm khuyến mãi: " + e.getMessage());
            return "promotion/add";
        }
    }

    // ✅ Tìm kiếm
    @GetMapping("/search/name")
    public String searchByName(@RequestParam String name, Model model) {
        List<Promotion> promotions = promotionService.searchByName(name);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name");
        model.addAttribute("searchValue", name);
        return "promotion/list";
    }

    @GetMapping("/search/type")
    public String searchByType(@RequestParam String type, Model model) {
        List<Promotion> promotions = promotionService.searchByType(type);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "type");
        model.addAttribute("searchValue", type);
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
        return "promotion/list";
    }
}
