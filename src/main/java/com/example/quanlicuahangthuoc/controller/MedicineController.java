// MedicineController.java
package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchMedicines(
            @RequestParam(required = false) String name) {
        List<Medicine> medicines = medicineService.searchMedicinesByName(name);
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Medicine>> getMedicinesByType(
            @PathVariable Medicine.MedicineType type) {
        List<Medicine> medicines = medicineService.getMedicinesByType(type);
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<List<Medicine>> getMedicinesBySupplier(
            @PathVariable Medicine.Supplier supplier) {
        List<Medicine> medicines = medicineService.getMedicinesBySupplier(supplier);
        return ResponseEntity.ok(medicines);
    }

   
}