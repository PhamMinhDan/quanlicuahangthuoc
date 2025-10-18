package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/staffs")
public class StaffController {

    @Autowired
    private StaffService staffService;
    @DeleteMapping("/{id}")
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