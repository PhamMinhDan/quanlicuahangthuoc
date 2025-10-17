// MedicineController.java
package com.example.quanlicuahangthuoc.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<?> getAllMedicines(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        
        // phân trangtrang
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

    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@Valid @RequestBody Medicine medicine) {
        Medicine savedMedicine = medicineService.saveMedicine(medicine);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMedicine);
    }
}