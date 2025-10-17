package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        try {
            List<Medicine> medicines = medicineService.getAllMedicines();
            return medicines != null ? ResponseEntity.ok(medicines) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("API is working!");
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Medicine>> searchByName(@RequestParam(required = false) String name) {
        try {
            List<Medicine> medicines = medicineService.searchByName(name);
            return medicines != null ? ResponseEntity.ok(medicines) : ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search/type")
    public ResponseEntity<List<Medicine>> searchByType(@RequestParam(required = false) String type) {
        try {
            List<Medicine> medicines = medicineService.searchByType(type);
            return medicines != null ? ResponseEntity.ok(medicines) : ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search/supplier")
    public ResponseEntity<List<Medicine>> searchBySupplier(@RequestParam(required = false) String supplier) {
        try {
            List<Medicine> medicines = medicineService.searchBySupplier(supplier);
            return medicines != null ? ResponseEntity.ok(medicines) : ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String supplier) {
        try {
            List<Medicine> medicines = medicineService.searchByFilters(name, type, supplier);
            return medicines != null ? ResponseEntity.ok(medicines) : ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMedicine(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("supplier") String supplier,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "description", required = false) String description) {

        try {
            String[] validTypes = {"Giảm đau", "Kháng sinh", "Chống viêm", "Thuốc hạ huyết áp"};
            String[] validSuppliers = {"Pfizer", "Novartis", "Johnson & Johnson", "Roche", "Merck & Co.", "Sanofi"};
            if (!contains(validTypes, type)) {
                return ResponseEntity.badRequest().body("Loại thuốc không hợp lệ.");
            }
            if (!contains(validSuppliers, supplier)) {
                return ResponseEntity.badRequest().body("Nhà cung cấp không hợp lệ.");
            }

            if (name == null || name.length() > 30) {
                return ResponseEntity.badRequest().body("Tên thuốc không được để trống và không vượt quá 30 ký tự.");
            }
            if (description != null && description.length() > 100) {
                return ResponseEntity.badRequest().body("Mô tả không vượt quá 100 ký tự.");
            }

            medicineService.createMedicine(name, type, expiryDate, price, stockQuantity, supplier, image, description);
            return ResponseEntity.ok("Thêm thuốc thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    private boolean contains(String[] array, String value) {
        if (value == null) return false;
        for (String item : array) {
            if (item.equals(value)) return true;
        }
        return false;
    }
}