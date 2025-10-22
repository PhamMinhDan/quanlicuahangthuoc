package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public boolean deletePromotion(Integer id) {
        if (!promotionRepository.existsById(id)) {
            return false;
        }
        promotionRepository.deleteById(id);
        return true;
    }
}




