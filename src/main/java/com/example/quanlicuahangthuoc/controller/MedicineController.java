package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@Validated
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping("/list")
    public ResponseEntity<?> getAllMedicines(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            Page<Medicine> medicinePage = medicineService.getAllMedicinesPaginated(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(medicinePage);
        } else {
            List<Medicine> medicines = medicineService.getAllMedicinesSorted(sortBy, sortDirection);
            return ResponseEntity.ok(medicines);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("API is working!");
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Medicine>> searchByName(@RequestParam(required = false) String name) {
        List<Medicine> medicines = medicineService.searchByName(name);
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/search/type")
    public ResponseEntity<List<Medicine>> searchByType(@RequestParam(required = false) String type) {
        List<Medicine> medicines = medicineService.searchByType(type);
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/search/supplier")
    public ResponseEntity<List<Medicine>> searchBySupplier(@RequestParam(required = false) String supplier) {
        List<Medicine> medicines = medicineService.searchBySupplier(supplier);
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String supplier) {
        List<Medicine> medicines = medicineService.searchByFilters(name, type, supplier);
        return ResponseEntity.ok(medicines);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createMedicine(@Valid @RequestBody Medicine medicine) {
        try {
            Medicine createdMedicine = medicineService.createMedicine(medicine);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMedicine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Integer id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok("Delete successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}