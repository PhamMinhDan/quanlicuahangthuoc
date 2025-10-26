package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    // CREATE - Tạo khuyến mãi mới
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    // READ - Lấy tất cả khuyến mãi (SẮP XẾP DESC theo ID)
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // READ - Lấy tất cả khuyến mãi với sắp xếp
    public List<Promotion> getAllPromotionsSorted(String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        return promotionRepository.findAll(sort);
    }

    // READ - Lấy khuyến mãi với phân trang (MẶC ĐỊNH DESC theo ID)
    public Page<Promotion> getAllPromotionsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return promotionRepository.findAll(pageable);
    }

    // READ - Lấy khuyến mãi với phân trang và sắp xếp
    public Page<Promotion> getAllPromotionsPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return promotionRepository.findAll(pageable);
    }

    // SEARCH - Tìm kiếm khuyến mãi theo tên (SẮP XẾP DESC)
    public List<Promotion> searchPromotionsByName(String name) {
        List<Promotion> promotions = promotionRepository.findByNameContainingIgnoreCase(name);
        return promotions.stream()
                .sorted(Comparator.comparing(Promotion::getId).reversed())
                .collect(Collectors.toList());
    }

    // UTILITY - Kiểm tra khuyến mãi có tồn tại không
    public boolean existsById(Integer id) {
        return promotionRepository.existsById(id);
    }

    // ADD - Thêm khuyến mãi mới
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

    // SEARCH - Tìm kiếm theo tên (SẮP XẾP DESC theo ID)
    public List<Promotion> searchByName(String name) {
        List<Promotion> promotions = promotionRepository.findByNameContaining(name);
        return promotions.stream()
                .sorted(Comparator.comparing(Promotion::getId).reversed())
                .collect(Collectors.toList());
    }

    // SEARCH - Tìm kiếm theo loại (SẮP XẾP DESC theo ID)
    public List<Promotion> searchByType(Promotion.PromotionType type) {
        List<Promotion> promotions = type != null ? promotionRepository.findByType(type) : promotionRepository.findAll();
        return promotions.stream()
                .sorted(Comparator.comparing(Promotion::getId).reversed())
                .collect(Collectors.toList());
    }

    // SEARCH - Tìm kiếm theo cả tên và loại (SẮP XẾP DESC theo ID)
    public List<Promotion> searchByNameAndType(String name, Promotion.PromotionType type) {
        List<Promotion> promotions = type != null ? promotionRepository.findByNameContainingAndType(name, type) : promotionRepository.findByNameContaining(name);
        return promotions.stream()
                .sorted(Comparator.comparing(Promotion::getId).reversed())
                .collect(Collectors.toList());
    }

    // SEARCH - Tìm kiếm theo tên hoặc loại (SẮP XẾP DESC theo ID)
    public List<Promotion> searchByNameOrType(String name, Promotion.PromotionType type) {
        List<Promotion> promotions = type != null ? promotionRepository.findByNameContainingOrType(name, type) : promotionRepository.findByNameContaining(name);
        return promotions.stream()
                .sorted(Comparator.comparing(Promotion::getId).reversed())
                .collect(Collectors.toList());
    }

    // DELETE - Xóa khuyến mãi
    public boolean deletePromotion(Integer id) {
        if (!promotionRepository.existsById(id)) {
            return false;
        }
        promotionRepository.deleteById(id);
        return true;
    }

    // GET - Lấy khuyến mãi theo ID
    public Promotion getPromotionById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + id));
    }

    // UPDATE - Cập nhật khuyến mãi
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