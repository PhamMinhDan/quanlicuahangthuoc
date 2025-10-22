package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    // Tìm kiếm khuyến mãi theo tên (không phân biệt hoa thường)
    List<Promotion> findByNameContainingIgnoreCase(String name);
}