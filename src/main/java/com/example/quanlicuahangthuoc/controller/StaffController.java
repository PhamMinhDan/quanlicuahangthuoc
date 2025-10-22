package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/view-staff")
    public String viewStaff(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            Model model) {
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
            model.addAttribute("activeNav", "staff");
            return "staff";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
            return "staff";
        }
    }

    @GetMapping("/add")
    public String showAddStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
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
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
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
            staffService.addStaff(staff);
            model.addAttribute("message", "Thêm nhân viên thành công");
            return "redirect:/api/staff/view-staff";
        } catch (IllegalStateException e) {
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
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditStaffForm(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            Model model) {
        try {
            Staff staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role);
            model.addAttribute("activeNav", "staff");
            return "staff-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Nhân viên không tồn tại: " + e.getMessage());
            return viewStaff(name, role, page, size, sortBy, sortDirection, model);
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
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
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
            staff.setId(id); // Đảm bảo ID được gán
            staffService.updateStaff(staff);
            model.addAttribute("message", "Cập nhật nhân viên thành công");
            return "redirect:/api/staff/view-staff";
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
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) String role,
            Model model) {
        try {
            staffService.deleteStaff(id);
            model.addAttribute("message", "Xóa nhân viên thành công");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Nhân viên không tồn tại: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi server: " + e.getMessage());
        }
        return "redirect:/api/staff/view-staff";
    }
}