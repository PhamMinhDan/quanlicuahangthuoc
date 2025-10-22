package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public Promotion addPromotion(Promotion promotion) {
        if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống");
        }
        
        if (promotion.getDiscountPercent() == null) {
            throw new IllegalArgumentException("Phần trăm giảm giá không được để trống");
        }
        
        if (promotion.getDiscountPercent() < 0 || promotion.getDiscountPercent() > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng 0 - 100");
        }
        
        return promotionRepository.save(promotion);
    }
}




