package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public Promotion getPromotionById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + id));
    }

    public Promotion updatePromotion(Integer id, Promotion updatedPromotion) {
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + id));

        if (updatedPromotion.getName() == null || updatedPromotion.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống");
        }

        Double discount = updatedPromotion.getDiscountPercent();
        if (discount == null || discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng 0 - 100");
        }

        existingPromotion.setName(updatedPromotion.getName());
        existingPromotion.setType(updatedPromotion.getType());
        existingPromotion.setDiscountPercent(updatedPromotion.getDiscountPercent());
        existingPromotion.setExpiredDate(updatedPromotion.getExpiredDate());

        return promotionRepository.save(existingPromotion);
    }
}




