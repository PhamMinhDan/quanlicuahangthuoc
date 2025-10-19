package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/staff")
@Validated
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/view-staff")
    public String viewStaff(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "role", required = false) Staff.Role role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            Model model) {
        try {
            Page<Staff> staffPage;
            if (name != null && !name.isEmpty() && role != null) {
                staffPage = staffService.searchStaffByNameAndRole(name, role, page, size, sortBy, sortDirection);
            } else if (name != null && !name.isEmpty()) {
                staffPage = staffService.searchStaffByName(name, page, size, sortBy, sortDirection);
            } else if (role != null) {
                staffPage = staffService.getStaffByRole(role, page, size, sortBy, sortDirection);
            } else {
                staffPage = staffService.getStaffPage(page, size, sortBy, sortDirection);
            }

            // Thêm dữ liệu cho summary cards
            model.addAttribute("totalStaff", staffService.getTotalStaff());
model.addAttribute("totalStaff", staffService.getTotalStaff());
model.addAttribute("totalManagers", staffService.getTotalManagers());

            // Thêm dữ liệu cho view
            model.addAttribute("staffs", staffPage.getContent());
            model.addAttribute("currentPage", staffPage.getNumber());
            model.addAttribute("totalPages", staffPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("name", name);
            model.addAttribute("role", role != null ? role.name() : null);
            model.addAttribute("activeNav", "staff");

            return "staff"; // Resolve sang staff.html
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
            return "staff";
        }
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Page<Staff>> getStaffList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        try {
            Page<Staff> staffPage = staffService.getStaffPage(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(staffPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Page<Staff>> searchStaffByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Page<Staff> staffPage = staffService.searchStaffByName(name, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(staffPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/role/{role}")
    @ResponseBody
    public ResponseEntity<Page<Staff>> getStaffByRole(
            @PathVariable Staff.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Page<Staff> staffPage = staffService.getStaffByRole(role, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(staffPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/role-name")
    @ResponseBody
    public ResponseEntity<Page<Staff>> searchStaffByNameAndRole(
            @RequestParam String name,
            @RequestParam Staff.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Page<Staff> staffPage = staffService.searchStaffByNameAndRole(name, role, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(staffPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
     @ResponseBody
    public ResponseEntity<?> addStaff(@Valid @RequestBody Staff staff) {
        try {
            Staff newStaff = staffService.addStaff(staff);
            return new ResponseEntity<>(newStaff, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi trong quá trình thêm nhân viên: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStaff(@PathVariable Integer id, @Valid @RequestBody Staff staff) {
        try {
            staff.setId(id);
            Staff updatedStaff = staffService.updateStaff(staff);
            return new ResponseEntity<>(updatedStaff, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi cập nhật nhân viên: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa nhân viên: " + e.getMessage());
        }
    }
}