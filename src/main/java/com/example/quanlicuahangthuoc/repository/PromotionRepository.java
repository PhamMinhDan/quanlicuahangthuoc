package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    // Tìm kiếm theo tên (tìm kiếm gần đúng)
    @Query("SELECT p FROM Promotion p WHERE p.name LIKE %:name%")
    List<Promotion> findByNameContaining(@Param("name") String name);
    
    // Tìm kiếm theo loại
    List<Promotion> findByType(String type);
    
    // Tìm kiếm theo cả tên và loại
    @Query("SELECT p FROM Promotion p WHERE p.name LIKE %:name% AND p.type = :type")
    List<Promotion> findByNameContainingAndType(@Param("name") String name, @Param("type") String type);
    
    // Tìm kiếm theo tên hoặc loại
    @Query("SELECT p FROM Promotion p WHERE p.name LIKE %:name% OR p.type = :type")
    List<Promotion> findByNameContainingOrType(@Param("name") String name, @Param("type") String type);
}
