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
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
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
            @RequestParam(defaultValue = "desc") String sortOrder, // Mặc định là desc để mới nhất lên đầu
            Model model, Authentication authentication) {

        Page<Promotion> promotionPage;
        if (sortBy != null && !sortBy.isEmpty()) {
            promotionPage = promotionService.getAllPromotionsPaginatedAndSorted(page, size, sortBy, sortOrder);
        } else {
            // Mặc định sắp xếp theo ID hoặc thời gian tạo giảm dần (nếu có trường createdDate)
            promotionPage = promotionService.getAllPromotionsPaginatedAndSorted(page, size, "id", "desc");
        }

        List<Promotion> allPromotions = promotionPage.getContent();
        model.addAttribute("promotions", allPromotions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promotionPage.getTotalPages());
        model.addAttribute("totalItems", promotionPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy != null && !sortBy.isEmpty() ? sortBy : "id");
        model.addAttribute("sortOrder", sortOrder);
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
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");

        return "promotion/list";
    }

    @GetMapping("/{id}")
    public String viewPromotion(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "promotions");
            return "promotion/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotions";
        }
    }

    @GetMapping("/delete/{id}")
    public String showDeleteModal(@PathVariable Integer id, Model model, Authentication authentication) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("promotion", new Promotion()); // Khởi tạo mặc định để tránh null
        }
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");
        return "promotion/list :: deleteModal"; // Trả về fragment modal
    }

    @PostMapping("/delete/{id}")
    public String deletePromotion(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchType", required = false) String searchType,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        // Kiểm tra quyền
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa khuyến mãi.");
            return buildRedirectUrl(page, size, sortBy, sortOrder, searchName, searchType, null);
        }

        try {
            boolean deleted = promotionService.deletePromotion(id);
            if (deleted) {
                // Xóa thành công
                return buildRedirectUrl(page, size, sortBy, sortOrder, searchName, searchType, "deleteSuccess");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi có ID: " + id);
                return buildRedirectUrl(page, size, sortBy, sortOrder, searchName, searchType, null);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa khuyến mãi: " + e.getMessage());
            return buildRedirectUrl(page, size, sortBy, sortOrder, searchName, searchType, null);
        }
    }

    // Helper method để build redirect URL
    private String buildRedirectUrl(int page, int size, String sortBy, String sortOrder,
                                    String searchName, String searchType, String successParam) {
        StringBuilder url = new StringBuilder("redirect:/promotions?page=" + page + "&size=" + size);

        if (sortBy != null && !sortBy.isEmpty()) {
            url.append("&sortBy=").append(sortBy);
        }
        if (sortOrder != null && !sortOrder.isEmpty()) {
            url.append("&sortOrder=").append(sortOrder);
        }
        if (searchName != null && !searchName.isEmpty()) {
            url.append("&searchName=").append(searchName);
        }
        if (searchType != null && !searchType.isEmpty()) {
            url.append("&searchType=").append(searchType);
        }
        if (successParam != null) {
            url.append("&").append(successParam).append("=true");
        }

        return url.toString();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
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
            Model model,
            Authentication authentication) {
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");

        if (result.hasErrors()) {
            if (promotion.getExpiredDate() == null) {
                model.addAttribute("error", "Vui lòng chọn ngày hết hạn hợp lệ!");
            }
            return "promotion/edit";
        }

        try {
            if (promotion.getExpiredDate() == null || promotion.getExpiredDate().isBefore(LocalDate.now().plusDays(1))) {
                model.addAttribute("error", "Ngày hết hạn phải từ ngày mai trở đi!");
                return "promotion/edit";
            }
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
    public String showAddForm(Model model, Authentication authentication) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");
        return "promotion/add";
    }

    @PostMapping
    public String addPromotion(
            @Valid @ModelAttribute("promotion") Promotion promotion,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            Authentication authentication) {
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");

        if (result.hasErrors()) {
            // Kiểm tra lỗi cụ thể cho expiredDate
            if (promotion.getExpiredDate() == null) {
                model.addAttribute("error", "Vui lòng chọn ngày hết hạn hợp lệ!");
            }
            return "promotion/add";
        }

        try {
            if (promotion.getExpiredDate() == null || promotion.getExpiredDate().isBefore(LocalDate.now().plusDays(1))) {
                model.addAttribute("error", "Ngày hết hạn phải từ ngày mai trở đi!");
                return "promotion/add";
            }
            promotionService.addPromotion(promotion);
            return "redirect:/promotions?page=0&size=10&sortBy=id&sortOrder=desc&addSuccess=true";
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
            Model model,
            Authentication authentication) {

        List<Promotion> promotions;
        boolean hasName = searchName != null && !searchName.trim().isEmpty();
        boolean hasType = searchType != null && !searchType.trim().isEmpty();
        Promotion.PromotionType type = hasType ? Promotion.PromotionType.valueOf(searchType.trim()) : null;

        if (!hasName && !hasType) {
            promotions = promotionService.getAllPromotions();
        } else if (hasName && hasType) {
            promotions = promotionService.searchByNameAndType(searchName.trim(), type);
        } else if (hasName) {
            promotions = promotionService.searchByName(searchName.trim());
        } else {
            promotions = promotionService.searchByType(type);
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
        model.addAttribute("searchType", searchType); // Giữ nguyên để hiển thị lại trong form
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");

        return "promotion/list";
    }
    @GetMapping("/search/name")
    public String searchByName(@RequestParam String name, Model model, Authentication authentication) {
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
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }

    @GetMapping("/search/type")
    public String searchByType(@RequestParam String type, Model model, Authentication authentication) {
        Promotion.PromotionType promotionType = type != null && !type.trim().isEmpty() ? Promotion.PromotionType.valueOf(type.trim()) : null;
        List<Promotion> promotions = promotionService.searchByType(promotionType);

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
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }

    @GetMapping("/search/name-and-type")
    public String searchByNameAndType(
            @RequestParam String name,
            @RequestParam String type,
            Model model, Authentication authentication) {
        Promotion.PromotionType promotionType = type != null && !type.trim().isEmpty() ? Promotion.PromotionType.valueOf(type.trim()) : null;
        List<Promotion> promotions = promotionService.searchByNameAndType(name, promotionType);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name-and-type");
        model.addAttribute("searchName", name);
        model.addAttribute("searchTypeValue", type);
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }

    @GetMapping("/search/name-or-type")
    public String searchByNameOrType(
            @RequestParam String name,
            @RequestParam String type,
            Model model, Authentication authentication) {
        Promotion.PromotionType promotionType = type != null && !type.trim().isEmpty() ? Promotion.PromotionType.valueOf(type.trim()) : null;
        List<Promotion> promotions = promotionService.searchByNameOrType(name, promotionType);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name-or-type");
        model.addAttribute("searchName", name);
        model.addAttribute("searchTypeValue", type);
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "promotions");
        return "promotion/list";
    }
}