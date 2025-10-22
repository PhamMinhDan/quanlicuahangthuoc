package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    // CREATE - Tạo khuyến mãi mới
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    
    // READ - Lấy tất cả khuyến mãi
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    // READ - Lấy khuyến mãi theo ID
    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
    }
    
    // READ - Lấy khuyến mãi với phân trang
    public Page<Promotion> getAllPromotionsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionRepository.findAll(pageable);
    }
    
    // READ - Lấy khuyến mãi với phân trang và sắp xếp
    public Page<Promotion> getAllPromotionsPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return promotionRepository.findAll(pageable);
    }
    
    // UPDATE - Cập nhật khuyến mãi
    public Promotion updatePromotion(Integer id, Promotion promotionDetails) {
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);
        if (optionalPromotion.isPresent()) {
            Promotion promotion = optionalPromotion.get();
            promotion.setName(promotionDetails.getName());
            promotion.setType(promotionDetails.getType());
            promotion.setDiscountPercent(promotionDetails.getDiscountPercent());
            promotion.setExpiredDate(promotionDetails.getExpiredDate());
            return promotionRepository.save(promotion);
        }
        return null;
    }
    
    // DELETE - Xóa khuyến mãi
    public boolean deletePromotion(Integer id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // SEARCH - Tìm kiếm khuyến mãi theo tên
    public List<Promotion> searchPromotionsByName(String name) {
        return promotionRepository.findByNameContainingIgnoreCase(name);
    }
    
    // UTILITY - Kiểm tra khuyến mãi có tồn tại không
    public boolean existsById(Integer id) {
        return promotionRepository.existsById(id);
    }
}
