package com.example.quanlicuahangthuoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.quanlicuahangthuoc.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {

    // Định nghĩa duy nhất cho từng method
    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByTypeContainingIgnoreCase(String type);

    List<Medicine> findBySupplierContainingIgnoreCase(String supplier);

    @Query("SELECT m FROM Medicine m WHERE " +
            "(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:type IS NULL OR LOWER(m.type) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
            "(:supplier IS NULL OR LOWER(m.supplier) LIKE LOWER(CONCAT('%', :supplier, '%')))")
    List<Medicine> findByFilters(String name, String type, String supplier);
}