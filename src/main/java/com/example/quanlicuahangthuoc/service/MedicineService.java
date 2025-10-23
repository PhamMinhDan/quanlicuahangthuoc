package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    public Page<Medicine> getAllMedicinesPaginated(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return medicineRepository.findAll(PageRequest.of(page, size, sort));
    }

    public Page<Medicine> searchByFiltersPaginated(String searchName, String searchType, String searchSupplier, int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Medicine.MedicineType type = null;
        Medicine.Supplier supplier = null;

        // Kiểm tra và chuyển đổi searchType
        if (searchType != null && !searchType.isEmpty()) {
            try {
                type = Medicine.MedicineType.valueOf(searchType);
            } catch (IllegalArgumentException e) {
                // Nếu không khớp, bỏ qua filter type
                type = null;
            }
        }

        // Kiểm tra và chuyển đổi searchSupplier
        if (searchSupplier != null && !searchSupplier.isEmpty()) {
            try {
                supplier = Medicine.Supplier.valueOf(searchSupplier);
            } catch (IllegalArgumentException e) {
                // Nếu không khớp, bỏ qua filter supplier
                supplier = null;
            }
        }

        return medicineRepository.findByFilters(searchName, type, supplier, PageRequest.of(page, size, sort));
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(Integer id) {
        return medicineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy thuốc với ID: " + id));
    }

    public void createMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }

    public void updateMedicine(Integer id, Medicine medicine) {
        Medicine existing = getMedicineById(id);
        existing.setName(medicine.getName());
        existing.setType(medicine.getType());
        existing.setExpiryDate(medicine.getExpiryDate());
        existing.setPrice(medicine.getPrice());
        existing.setStockQuantity(medicine.getStockQuantity());
        existing.setSupplier(medicine.getSupplier());
        existing.setImage(medicine.getImage());
        medicineRepository.save(existing);
    }

    public void deleteMedicine(Integer id) {
        medicineRepository.deleteById(id);
    }

    // Thêm phương thức để lấy tổng số thuốc
    public long getTotalMedicinesCount() {
        return medicineRepository.count();
    }

    // Thêm phương thức để lấy tổng số nhà cung cấp khác nhau
    public long getTotalSuppliersCount() {
        return medicineRepository.countDistinctSuppliers();
    }

    // Thêm phương thức để lấy tổng stockQuantity
    public Integer getTotalStockQuantity() {
        return medicineRepository.sumStockQuantity();
    }
}