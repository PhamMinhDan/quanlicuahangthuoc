// MedicineController.java
// MedicineController.java
package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    // Constructor thủ công thay thế @RequiredArgsConstructor
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = medicineService.getAllMedicines();
        return ResponseEntity.ok(medicines);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Integer id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok("Delete successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Integer id, @RequestBody Medicine medicine) {
        try {
            Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
            return ResponseEntity.ok(updatedMedicine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}