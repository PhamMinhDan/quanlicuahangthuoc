package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    // Lấy tất cả khuyến mãi
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    // Tìm kiếm theo tên
    public List<Promotion> searchByName(String name) {
        return promotionRepository.findByNameContaining(name);
    }
    
    // Tìm kiếm theo loại
    public List<Promotion> searchByType(String type) {
        return promotionRepository.findByType(type);
    }
    
    // Tìm kiếm theo cả tên và loại
    public List<Promotion> searchByNameAndType(String name, String type) {
        return promotionRepository.findByNameContainingAndType(name, type);
    }
    
    // Tìm kiếm theo tên hoặc loại
    public List<Promotion> searchByNameOrType(String name, String type) {
        return promotionRepository.findByNameContainingOrType(name, type);
    }
}