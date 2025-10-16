// MedicineService.java
package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    // Tìm kiếm theo tên thuốc
    public List<Medicine> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllMedicines();
        }
        return medicineRepository.findByNameContainingIgnoreCase(name.trim());
    }

    // Tìm kiếm theo loại thuốc
    public List<Medicine> searchByType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return getAllMedicines();
        }
        
        try {
            Medicine.MedicineType type = Medicine.MedicineType.valueOf(typeStr.trim());
            return medicineRepository.findByType(type);
        } catch (IllegalArgumentException e) {
            return getAllMedicines(); // Trả về tất cả nếu enum không hợp lệ
        }
    }

    // Tìm kiếm theo nhà cung cấp
    public List<Medicine> searchBySupplier(String supplierStr) {
        if (supplierStr == null || supplierStr.trim().isEmpty()) {
            return getAllMedicines();
        }
        
        try {
            Medicine.Supplier supplier = Medicine.Supplier.valueOf(supplierStr.trim());
            return medicineRepository.findBySupplier(supplier);
        } catch (IllegalArgumentException e) {
            return getAllMedicines(); // Trả về tất cả nếu enum không hợp lệ
        }
    }

    // Tìm kiếm theo nhiều bộ lọc
    public List<Medicine> searchByFilters(String name, String typeStr, String supplierStr) {
        Medicine.MedicineType type = null;
        Medicine.Supplier supplier = null;
        
        // Parse type enum
        if (typeStr != null && !typeStr.trim().isEmpty()) {
            try {
                type = Medicine.MedicineType.valueOf(typeStr.trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid type
            }
        }
        
        // Parse supplier enum
        if (supplierStr != null && !supplierStr.trim().isEmpty()) {
            try {
                supplier = Medicine.Supplier.valueOf(supplierStr.trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid supplier
            }
        }
        
        return medicineRepository.findByFilters(
            (name != null && !name.trim().isEmpty()) ? name.trim() : null,
            type,
            supplier
        );
    }
}