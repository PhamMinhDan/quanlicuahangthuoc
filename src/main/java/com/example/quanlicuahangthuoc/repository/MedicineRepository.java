package com.example.quanlicuahangthuoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlicuahangthuoc.entity.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    List<Medicine> findByNameContainingIgnoreCase(String name);
    List<Medicine> findByType(Medicine.MedicineType type);
    List<Medicine> findBySupplier(Medicine.Supplier supplier);
}