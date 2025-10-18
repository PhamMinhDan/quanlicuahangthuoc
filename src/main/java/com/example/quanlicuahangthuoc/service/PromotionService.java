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

@Service
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    // Lấy tất cả khuyến mãi
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    // Lấy khuyến mãi với phân trang (mặc định 10 items/trang)
    public Page<Promotion> getAllPromotionsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionRepository.findAll(pageable);
    }
    
    // Lấy khuyến mãi với phân trang và sắp xếp
    public Page<Promotion> getAllPromotionsPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return promotionRepository.findAll(pageable);
    }
    
    // Lấy tất cả khuyến mãi với sắp xếp
    public List<Promotion> getAllPromotionsSorted(String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        switch (sortBy.toLowerCase()) {
            case "name":
                return "desc".equalsIgnoreCase(sortOrder) ? 
                    promotionRepository.findAllByOrderByNameDesc() : 
                    promotionRepository.findAllByOrderByNameAsc();
            case "validityperiod":
            case "validity_period":
                return "desc".equalsIgnoreCase(sortOrder) ? 
                    promotionRepository.findAllByOrderByValidityPeriodDesc() : 
                    promotionRepository.findAllByOrderByValidityPeriodAsc();
            default:
                return promotionRepository.findAll();
        }
    }
    
    // Sắp xếp theo tên tăng dần
    public List<Promotion> getPromotionsSortedByNameAsc() {
        return promotionRepository.findAllByOrderByNameAsc();
    }
    
    // Sắp xếp theo tên giảm dần
    public List<Promotion> getPromotionsSortedByNameDesc() {
        return promotionRepository.findAllByOrderByNameDesc();
    }
    
    // Sắp xếp theo thời hạn tăng dần
    public List<Promotion> getPromotionsSortedByValidityPeriodAsc() {
        return promotionRepository.findAllByOrderByValidityPeriodAsc();
    }
    
    // Sắp xếp theo thời hạn giảm dần
    public List<Promotion> getPromotionsSortedByValidityPeriodDesc() {
        return promotionRepository.findAllByOrderByValidityPeriodDesc();
    }
    
    // Phân trang với sắp xếp theo tên tăng dần
    public Page<Promotion> getPromotionsPaginatedSortedByNameAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionRepository.findAllByOrderByNameAsc(pageable);
    }
    
    // Phân trang với sắp xếp theo tên giảm dần
    public Page<Promotion> getPromotionsPaginatedSortedByNameDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionRepository.findAllByOrderByNameDesc(pageable);
    }
    
    // Phân trang với sắp xếp theo thời hạn tăng dần
    public Page<Promotion> getPromotionsPaginatedSortedByValidityPeriodAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionRepository.findAllByOrderByValidityPeriodAsc(pageable);
    }
    
    // Phân trang với sắp xếp theo thời hạn giảm dần
    public Page<Promotion> getPromotionsPaginatedSortedByValidityPeriodDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionRepository.findAllByOrderByValidityPeriodDesc(pageable);
    }
}
