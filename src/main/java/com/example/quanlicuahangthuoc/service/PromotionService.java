package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // Xóa khuyến mãi
    public boolean deletePromotion(Integer id) {
        if (!promotionRepository.existsById(id)) {
            return false;
        }
        promotionRepository.deleteById(id);
        return true;
    }

    // ✅ Giữ lại chỉ 1 hàm getPromotionById
    public Promotion getPromotionById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + id));
    }

    // Cập nhật khuyến mãi
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
