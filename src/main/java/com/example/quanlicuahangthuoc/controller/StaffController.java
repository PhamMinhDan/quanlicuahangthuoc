package com.example.quanlicuahangthuoc.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    // Lấy tất cả nhân viên với phân trang và sắp xếp
    @GetMapping
    public ResponseEntity<Page<Staff>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Page<Staff> staffPage = staffService.getAllStaff(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(staffPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tìm kiếm nhân viên theo tên
    @GetMapping("/search")
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

    // Lọc nhân viên theo chức vụ
    @GetMapping("/role/{role}")
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

    // Tìm kiếm nhân viên theo cả tên và chức vụ
    @GetMapping("/search-by-name-and-role")
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

}