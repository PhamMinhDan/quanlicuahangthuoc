package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.config.FileUploadConfig;
import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/medicines")
@Validated
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @GetMapping("/view-medicine")
    public String getMedicinesPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchSupplier", required = false) String searchSupplier,
            Model model) {
        List<Medicine> medicines;
        if (searchName != null || searchType != null || searchSupplier != null) {
            medicines = medicineService.searchByFilters(searchName, searchType, searchSupplier);
            if ("name".equalsIgnoreCase(sortBy) || "price".equalsIgnoreCase(sortBy)) {
                medicines.sort((m1, m2) -> {
                    int direction = "desc".equalsIgnoreCase(sortDirection) ? -1 : 1;
                    if ("name".equalsIgnoreCase(sortBy)) {
                        return direction * m1.getName().compareToIgnoreCase(m2.getName());
                    } else {
                        return direction * Double.compare(m1.getPrice(), m2.getPrice());
                    }
                });
            }
            int totalItems = medicines.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int start = page * size;
            int end = Math.min(start + size, totalItems);
            medicines = medicines.subList(start >= totalItems ? totalItems : start, end);
            model.addAttribute("medicines", medicines);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
        } else {
            Page<Medicine> medicinePage = medicineService.getAllMedicinesPaginated(page, size, sortBy, sortDirection);
            medicines = medicinePage.getContent();
            model.addAttribute("medicines", medicines);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", medicinePage.getTotalPages());
            model.addAttribute("totalItems", medicinePage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
        }
        model.addAttribute("totalMedicines", medicineService.getAllMedicines().size());
        model.addAttribute("totalSuppliers", medicineService.getAllMedicines().stream()
                .map(medicine -> medicine.getSupplier().name())
                .distinct()
                .count());
        model.addAttribute("totalStock", medicineService.getAllMedicines().stream()
                .mapToInt(Medicine::getStockQuantity)
                .sum());
        model.addAttribute("activeNav", "medicines");
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchSupplier", searchSupplier);
        return "medicine";
    }

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
public String createMedicine(
        @Valid @ModelAttribute Medicine medicine,
        BindingResult bindingResult,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
        @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
        @RequestParam(value = "searchName", required = false) String searchName,
        @RequestParam(value = "searchType", required = false) String searchType,
        @RequestParam(value = "searchSupplier", required = false) String searchSupplier,
        Model model) {
    // Kiểm tra lỗi validation
    if (bindingResult.hasErrors()) {
        String errorMessage = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .reduce((e1, e2) -> e1 + "; " + e2)
                .orElse("Lỗi nhập liệu");
        model.addAttribute("error", errorMessage);
        model.addAttribute("medicine", medicine);
        model.addAttribute("showAddModal", true);
        return getMedicinesPage(page, size, sortBy, sortDirection, searchName, searchType, searchSupplier, model);
    }

    try {
        // Xử lý upload ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileUploadConfig.storeFile(imageFile);
            medicine.setImage(imagePath != null ? imagePath : "/images/default-medicine.jpg");
        } else {
            medicine.setImage("/images/default-medicine.jpg");
        }

        // Lưu vào database
        Medicine createdMedicine = medicineService.createMedicine(medicine);
        model.addAttribute("message", "Thêm thuốc thành công: " + createdMedicine.getName());
        return "redirect:/api/medicines/view-medicine?page=" + page +
               "&size=" + size +
               "&sortBy=" + sortBy +
               "&sortDirection=" + sortDirection +
               "&searchName=" + (searchName != null ? searchName : "") +
               "&searchType=" + (searchType != null ? searchType : "") +
               "&searchSupplier=" + (searchSupplier != null ? searchSupplier : "");
    } catch (IllegalArgumentException e) {
        model.addAttribute("error", "Lỗi: " + e.getMessage());
        model.addAttribute("medicine", medicine);
        model.addAttribute("showAddModal", true);
        return getMedicinesPage(page, size, sortBy, sortDirection, searchName, searchType, searchSupplier, model);
    } catch (IOException e) {
        model.addAttribute("error", "Lỗi upload file: " + e.getMessage());
        model.addAttribute("medicine", medicine);
        model.addAttribute("showAddModal", true);
        return getMedicinesPage(page, size, sortBy, sortDirection, searchName, searchType, searchSupplier, model);
    } catch (Exception e) {
        model.addAttribute("error", "Lỗi server: " + e.getMessage());
        model.addAttribute("medicine", medicine);
        model.addAttribute("showAddModal", true);
        return getMedicinesPage(page, size, sortBy, sortDirection, searchName, searchType, searchSupplier, model);
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

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Integer id, @Valid @RequestBody Medicine medicine) {
        try {
            Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
            return ResponseEntity.ok(updatedMedicine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}