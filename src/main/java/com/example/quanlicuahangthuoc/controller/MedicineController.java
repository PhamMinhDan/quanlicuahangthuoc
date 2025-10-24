package com.example.quanlicuahangthuoc.controller;


import com.example.quanlicuahangthuoc.repository.MedicineRepository;
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
import org.springframework.security.core.Authentication;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/medicines")
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
            Model model, Authentication authentication) {
        try {
            Page<Medicine> medicinePage;
            if (searchName != null || searchType != null || searchSupplier != null) {
                medicinePage = medicineService.searchByFiltersPaginated(searchName, searchType, searchSupplier, page, size, sortBy, sortDirection);
            } else {
                medicinePage = medicineService.getAllMedicinesPaginated(page, size, sortBy, sortDirection);
            }
            List<Medicine> medicines = medicinePage.getContent();
            model.addAttribute("medicines", medicines);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", medicinePage.getTotalPages());
            model.addAttribute("totalItems", medicinePage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("totalMedicines", medicineService.getTotalMedicinesCount());
            model.addAttribute("totalSuppliers", medicineService.getTotalSuppliersCount());
            model.addAttribute("totalStock", medicineService.getTotalStockQuantity());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "medicines");
            return "medicine";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách thuốc: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            return "medicine";
        }
    }

    @GetMapping("/add")
    public String showAddMedicineForm(Model model, Authentication authentication) {
        model.addAttribute("medicine", new Medicine());
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
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
            Model model) throws IOException {
        if (bindingResult.hasErrors()) {
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
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileUploadConfig.storeFile(imageFile);
            medicine.setImage(imagePath);
        }
        medicineService.createMedicine(medicine);
        model.addAttribute("message", "Thêm thuốc thành công");
        return "redirect:/medicines/view-medicine?page=" + page +
                "&size=" + size +
                "&sortBy=" + sortBy +
                "&sortDirection=" + sortDirection +
                (searchName != null ? "&searchName=" + searchName : "") +
                (searchType != null ? "&searchType=" + searchType : "") +
                (searchSupplier != null ? "&searchSupplier=" + searchSupplier : "");
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
            Model model, Authentication authentication) {
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
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "medicines");
            return "medicine-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Thuốc không tồn tại: " + e.getMessage());
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchSupplier", searchSupplier);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "medicines");
            return "medicine";
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
            Model model) throws IOException {
        if (bindingResult.hasErrors()) {
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
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileUploadConfig.storeFile(imageFile);
            medicine.setImage(imagePath);
        }
        try {
            medicineService.updateMedicine(id, medicine);
            model.addAttribute("message", "Cập nhật thuốc thành công");
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
        return "redirect:/medicines/view-medicine?page=" + page +
                "&size=" + size +
                "&sortBy=" + sortBy +
                "&sortDirection=" + sortDirection +
                (searchName != null ? "&searchName=" + searchName : "") +
                (searchType != null ? "&searchType=" + searchType : "") +
                (searchSupplier != null ? "&searchSupplier=" + searchSupplier : "");
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
        return "redirect:/medicines/view-medicine?page=" + page +
                "&size=" + size +
                "&sortBy=" + sortBy +
                "&sortDirection=" + sortDirection +
                (searchName != null ? "&searchName=" + searchName : "") +
                (searchType != null ? "&searchType=" + searchType : "") +
                (searchSupplier != null ? "&searchSupplier=" + searchSupplier : "");
    }
}