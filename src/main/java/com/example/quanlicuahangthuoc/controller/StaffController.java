package com.example.quanlicuahangthuoc.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;


    @GetMapping
    public ResponseEntity<?> getAllStaff(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            Page<Staff> staffPage = staffService.getStaffPage(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(staffPage);
        } else {
            List<Staff> staffList = staffService.getStaffListSorted(sortBy, sortDirection);
            return ResponseEntity.ok(staffList);
        }
    }
}