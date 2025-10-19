package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@CrossOrigin(origins = "*")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    // Lấy tất cả khuyến mãi
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }
    
    // Lấy khuyến mãi với phân trang (mặc định 10 items/trang)
    @GetMapping("/page")
    public ResponseEntity<Page<Promotion>> getAllPromotionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Promotion> promotions = promotionService.getAllPromotionsPaginated(page, size);
        return ResponseEntity.ok(promotions);
    }
    
    // Lấy khuyến mãi với phân trang và sắp xếp
    @GetMapping("/page/sorted")
    public ResponseEntity<Page<Promotion>> getAllPromotionsPaginatedAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Promotion> promotions = promotionService.getAllPromotionsPaginatedAndSorted(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(promotions);
    }
    
    // Lấy tất cả khuyến mãi với sắp xếp
    @GetMapping("/sorted")
    public ResponseEntity<List<Promotion>> getAllPromotionsSorted(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        List<Promotion> promotions = promotionService.getAllPromotionsSorted(sortBy, sortOrder);
        return ResponseEntity.ok(promotions);
    }
    
    // Sắp xếp theo tên tăng dần
    @GetMapping("/sort/name/asc")
    public ResponseEntity<List<Promotion>> getPromotionsSortedByNameAsc() {
        List<Promotion> promotions = promotionService.getPromotionsSortedByNameAsc();
        return ResponseEntity.ok(promotions);
    }
    
    // Sắp xếp theo tên giảm dần
    @GetMapping("/sort/name/desc")
    public ResponseEntity<List<Promotion>> getPromotionsSortedByNameDesc() {
        List<Promotion> promotions = promotionService.getPromotionsSortedByNameDesc();
        return ResponseEntity.ok(promotions);
    }
    
    // Sắp xếp theo thời hạn tăng dần
    @GetMapping("/sort/validityperiod/asc")
    public ResponseEntity<List<Promotion>> getPromotionsSortedByValidityPeriodAsc() {
        List<Promotion> promotions = promotionService.getPromotionsSortedByValidityPeriodAsc();
        return ResponseEntity.ok(promotions);
    }
    
    // Sắp xếp theo thời hạn giảm dần
    @GetMapping("/sort/validityperiod/desc")
    public ResponseEntity<List<Promotion>> getPromotionsSortedByValidityPeriodDesc() {
        List<Promotion> promotions = promotionService.getPromotionsSortedByValidityPeriodDesc();
        return ResponseEntity.ok(promotions);
    }
    
    // Phân trang với sắp xếp theo tên tăng dần
    @GetMapping("/page/sort/name/asc")
    public ResponseEntity<Page<Promotion>> getPromotionsPaginatedSortedByNameAsc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Promotion> promotions = promotionService.getPromotionsPaginatedSortedByNameAsc(page, size);
        return ResponseEntity.ok(promotions);
    }
    
    // Phân trang với sắp xếp theo tên giảm dần
    @GetMapping("/page/sort/name/desc")
    public ResponseEntity<Page<Promotion>> getPromotionsPaginatedSortedByNameDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Promotion> promotions = promotionService.getPromotionsPaginatedSortedByNameDesc(page, size);
        return ResponseEntity.ok(promotions);
    }
    
    // Phân trang với sắp xếp theo thời hạn tăng dần
    @GetMapping("/page/sort/validityperiod/asc")
    public ResponseEntity<Page<Promotion>> getPromotionsPaginatedSortedByValidityPeriodAsc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Promotion> promotions = promotionService.getPromotionsPaginatedSortedByValidityPeriodAsc(page, size);
        return ResponseEntity.ok(promotions);
    }
    
    // Phân trang với sắp xếp theo thời hạn giảm dần
    @GetMapping("/page/sort/validityperiod/desc")
    public ResponseEntity<Page<Promotion>> getPromotionsPaginatedSortedByValidityPeriodDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Promotion> promotions = promotionService.getPromotionsPaginatedSortedByValidityPeriodDesc(page, size);
        return ResponseEntity.ok(promotions);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addPromotion(@RequestBody Promotion promotion) {
        try {
            Promotion newPromotion = promotionService.addPromotion(promotion);
            return ResponseEntity.ok(newPromotion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi thêm khuyến mãi: " + e.getMessage());
        }
    }

}
