package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.config.FileUploadConfig;
import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/medicines")
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
        try {
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
            } else {
                Page<Medicine> medicinePage = medicineService.getAllMedicinesPaginated(page, size, sortBy, sortDirection);
                medicines = medicinePage.getContent();
                model.addAttribute("medicines", medicines);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", medicinePage.getTotalPages());
                model.addAttribute("totalItems", medicinePage.getTotalElements());
                model.addAttribute("pageSize", size);
            }
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("totalMedicines", medicineService.getAllMedicines().size());
            model.addAttribute("totalSuppliers", medicineService.getAllMedicines().stream()
                    .map(medicine -> medicine.getSupplier().name())
                    .distinct()
                    .count());
            model.addAttribute("totalStock", medicineService.getAllMedicines().stream()
                    .mapToInt(Medicine::getStockQuantity)
                    .sum());
            model.addAttribute("activeNav", "medicines");
            return "medicine";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách thuốc: " + e.getMessage());
            return "medicine";
        }
    }

    @GetMapping("/add")
    public String showAddMedicineForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        model.addAttribute("activeNav", "medicines");
        return "medicine-form";
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
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu");
            model.addAttribute("error", errorMessage);
            model.addAttribute("medicine", medicine);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileUploadConfig.storeFile(imageFile);
                medicine.setImage(imagePath != null ? imagePath : "/images/default-medicine.jpg");
            } else {
                medicine.setImage("/images/default-medicine.jpg");
            }
            medicineService.createMedicine(medicine);
            model.addAttribute("message", "Thêm thuốc thành công: " + medicine.getName());
            return "redirect:/api/medicines/view-medicine?page=" + page +
                   "&size=" + size +
                   "&sortBy=" + sortBy +
                   "&sortDirection=" + sortDirection +
                   (searchName != null ? "&searchName=" + searchName : "") +
                   (searchType != null ? "&searchType=" + searchType : "") +
                   (searchSupplier != null ? "&searchSupplier=" + searchSupplier : "");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi upload file: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditMedicineForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchSupplier", required = false) String searchSupplier,
            Model model) {
        try {
            Medicine medicine = medicineService.getMedicineById(id);
            model.addAttribute("medicine", medicine);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thuốc không tồn tại: " + e.getMessage());
            return getMedicinesPage(page, size, sortBy, sortDirection, searchName, searchType, searchSupplier, model);
        }
    }

    @PostMapping("/update/{id}")
    public String updateMedicine(
            @PathVariable Integer id,
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
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu");
            model.addAttribute("error", errorMessage);
            model.addAttribute("medicine", medicine);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileUploadConfig.storeFile(imageFile);
                medicine.setImage(imagePath != null ? imagePath : "/images/default-medicine.jpg");
            }
            medicineService.updateMedicine(id, medicine);
            model.addAttribute("message", "Cập nhật thuốc thành công");
            return "redirect:/api/medicines/view-medicine?page=" + page +
                   "&size=" + size +
                   "&sortBy=" + sortBy +
                   "&sortDirection=" + sortDirection +
                   (searchName != null ? "&searchName=" + searchName : "") +
                   (searchType != null ? "&searchType=" + searchType : "") +
                   (searchSupplier != null ? "&searchSupplier=" + searchSupplier : "");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thuốc không tồn tại: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi upload file: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
            model.addAttribute("medicine", medicine);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicine(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchSupplier", required = false) String searchSupplier,
            Model model) {
        try {
            medicineService.deleteMedicine(id);
            model.addAttribute("message", "Xóa thuốc thành công");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thuốc không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
        }
        return "redirect:/api/medicines/view-medicine?page=" + page +
               "&size=" + size +
               "&sortBy=" + sortBy +
               "&sortDirection=" + sortDirection +
               (searchName != null ? "&searchName=" + searchName : "") +
               (searchType != null ? "&searchType=" + searchType : "") +
               (searchSupplier != null ? "&searchSupplier=" + searchSupplier : "");
    }
}