// MedicineController.java
package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = medicineService.getAllMedicines();
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("API is working!");
    }

    // Tìm kiếm theo tên thuốc
    @GetMapping("/search/name")
    public ResponseEntity<List<Medicine>> searchByName(@RequestParam(required = false) String name) {
        List<Medicine> medicines = medicineService.searchByName(name);
        return ResponseEntity.ok(medicines);
    }

    // Tìm kiếm theo loại thuốc
    @GetMapping("/search/type")
    public ResponseEntity<List<Medicine>> searchByType(@RequestParam(required = false) String type) {
        List<Medicine> medicines = medicineService.searchByType(type);
        return ResponseEntity.ok(medicines);
    }

    // Tìm kiếm theo nhà cung cấp
    @GetMapping("/search/supplier")
    public ResponseEntity<List<Medicine>> searchBySupplier(@RequestParam(required = false) String supplier) {
        List<Medicine> medicines = medicineService.searchBySupplier(supplier);
        return ResponseEntity.ok(medicines);
    }

    // Tìm kiếm theo nhiều bộ lọc
    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String supplier) {
        List<Medicine> medicines = medicineService.searchByFilters(name, type, supplier);
        return ResponseEntity.ok(medicines);
    }

    // Thêm mới thuốc
    @PostMapping
    public ResponseEntity<?> createMedicine(@RequestBody Medicine medicine) {
        try {
            Medicine createdMedicine = medicineService.createMedicine(medicine);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMedicine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }
}