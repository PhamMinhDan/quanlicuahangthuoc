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
    public List<Medicine> searchMedicinesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllMedicines();
        }
        return medicineRepository.findByNameContainingIgnoreCase(name.trim());
    }

    public List<Medicine> getMedicinesByType(Medicine.MedicineType type) {
        return medicineRepository.findByType(type);
    }

    public List<Medicine> getMedicinesBySupplier(Medicine.Supplier supplier) {
        return medicineRepository.findBySupplier(supplier);
    }

    
}