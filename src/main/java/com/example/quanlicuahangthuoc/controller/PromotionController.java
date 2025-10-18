package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    // Tìm kiếm theo tên
    @GetMapping("/search/name")
    public ResponseEntity<List<Promotion>> searchByName(@RequestParam String name) {
        List<Promotion> promotions = promotionService.searchByName(name);
        return ResponseEntity.ok(promotions);
    }
    
    // Tìm kiếm theo loại
    @GetMapping("/search/type")
    public ResponseEntity<List<Promotion>> searchByType(@RequestParam String type) {
        List<Promotion> promotions = promotionService.searchByType(type);
        return ResponseEntity.ok(promotions);
    }
    
    // Tìm kiếm theo cả tên và loại
    @GetMapping("/search/name-and-type")
    public ResponseEntity<List<Promotion>> searchByNameAndType(
            @RequestParam String name, 
            @RequestParam String type) {
        List<Promotion> promotions = promotionService.searchByNameAndType(name, type);
        return ResponseEntity.ok(promotions);
    }
    
    // Tìm kiếm theo tên hoặc loại
    @GetMapping("/search/name-or-type")
    public ResponseEntity<List<Promotion>> searchByNameOrType(
            @RequestParam String name, 
            @RequestParam String type) {
        List<Promotion> promotions = promotionService.searchByNameOrType(name, type);
        return ResponseEntity.ok(promotions);
    }
}