package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;
import com.example.quanlicuahangthuoc.config.FileUploadConfig;
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
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @GetMapping("/view-staff")
    public String viewStaff(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            Model model, Authentication authentication) {
        try {
            Page<Staff> staffPage;
            if (name != null && !name.trim().isEmpty() && role != null && !role.trim().isEmpty()) {
                staffPage = staffService.searchStaffByNameAndRole(name, Staff.Role.valueOf(role), page, size, sortBy, sortDirection);
            } else if (name != null && !name.trim().isEmpty()) {
                staffPage = staffService.searchStaffByName(name, page, size, sortBy, sortDirection);
            } else if (role != null && !role.trim().isEmpty()) {
                staffPage = staffService.getStaffByRole(Staff.Role.valueOf(role), page, size, sortBy, sortDirection);
            } else {
                staffPage = staffService.getStaffPage(page, size, sortBy, sortDirection);
            }

            model.addAttribute("totalStaff", staffService.getTotalStaff());
            model.addAttribute("totalManagers", staffService.getTotalManagers());
            model.addAttribute("staffs", staffPage.getContent());
            model.addAttribute("currentPage", staffPage.getNumber());
            model.addAttribute("totalPages", staffPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "staff");
            return "staff";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            return "staff";
        }
    }

    @GetMapping("/add")
    public String showAddStaffForm(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            Model model,
            Authentication authentication) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("name", name);
        model.addAttribute("role", role);
        model.addAttribute("isManager", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
        model.addAttribute("activeNav", "staff");
        return "staff-form";
    }

    @PostMapping("/add")
    public String createStaff(
            @Valid @ModelAttribute Staff staff,
            BindingResult bindingResult,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            model.addAttribute("error", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu"));
            return "staff-form";
        }
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileUploadConfig.storeFile(imageFile);
                if (imagePath != null) {
                    staff.setImage(imagePath);
                }
            }
            staffService.addStaff(staff);
            // Chuyển hướng về trang đầu tiên (page=0), bỏ các tham số lọc
            return "redirect:/api/staff/view-staff?page=0" +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection +
                    "&addSuccess=true";
        } catch (IllegalStateException e) {
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            model.addAttribute("error", "Lỗi trạng thái: " + e.getMessage());
            return "staff-form";
        } catch (Exception e) {
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            model.addAttribute("error", "Lỗi khi upload ảnh hoặc xử lý: " + e.getMessage());
            return "staff-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditStaffForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            Model model, Authentication authentication) {
        try {
            Staff staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            model.addAttribute("activeNav", "staff");
            return "staff-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Nhân viên không tồn tại: " + e.getMessage());
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly")));
            return viewStaff(name, role, page, size, sortBy, sortDirection, model, authentication);
        }
    }

    @PostMapping("/update/{id}")
    public String updateStaff(
            @PathVariable Integer id,
            @Valid @ModelAttribute Staff staff,
            BindingResult bindingResult,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((e1, e2) -> e1 + "; " + e2)
                    .orElse("Lỗi nhập liệu"));
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            return "staff-form";
        }
        try {
            staff.setId(id);
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileUploadConfig.storeFile(imageFile);
                if (imagePath != null) {
                    staff.setImage(imagePath);
                }
            }
            staffService.updateStaff(staff);
            return "redirect:/api/staff/view-staff?page=" + page +
                    "&size=" + size +
                    "&sortBy=" + sortBy +
                    "&sortDirection=" + sortDirection +
                    (name != null ? "&name=" + name : "") +
                    (role != null ? "&role=" + role : "") +
                    "&updateSuccess=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            return "staff-form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            return "staff-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            Model model,
            Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
            model.addAttribute("error", "Bạn không có quyền xóa nhân viên.");
            return "redirect:/api/staff/view-staff?page=" + page + "&size=" + size + "&sortBy=" + sortBy + "&sortDirection=" + sortDirection +
                    (name != null ? "&name=" + name : "") +
                    (role != null ? "&role=" + role : "");
        }

        try {
            staffService.deleteStaff(id);

            long totalItems = staffService.getTotalStaff(name, role);
            int totalPages = (int) Math.ceil((double) totalItems / size);

            int adjustedPage = page;
            if (page >= totalPages && totalPages > 0) {
                adjustedPage = totalPages - 1;
            } else if (totalPages == 0) {
                adjustedPage = 0;
            }

            return "redirect:/api/staff/view-staff?page=" + adjustedPage + "&size=" + size + "&sortBy=" + sortBy + "&sortDirection=" + sortDirection +
                    (name != null ? "&name=" + name : "") +
                    (role != null ? "&role=" + role : "") +
                    "&deleteSuccess=true";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Nhân viên không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
        }
        return "redirect:/api/staff/view-staff?page=" + page + "&size=" + size + "&sortBy=" + sortBy + "&sortDirection=" + sortDirection +
                (name != null ? "&name=" + name : "") +
                (role != null ? "&role=" + role : "");
    }
}