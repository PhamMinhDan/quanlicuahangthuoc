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
    
    // SEARCH - Tìm kiếm khuyến mãi theo tên
    public List<Promotion> searchPromotionsByName(String name) {
        return promotionRepository.findByNameContainingIgnoreCase(name);
    }
    
    // UTILITY - Kiểm tra khuyến mãi có tồn tại không
    public boolean existsById(Integer id) {
        return promotionRepository.existsById(id);
    }

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

    public boolean deletePromotion(Integer id) {
        if (!promotionRepository.existsById(id)) {
            return false;
        }
        promotionRepository.deleteById(id);
        return true;
    }
}

