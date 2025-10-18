package com.example.quanlicuahangthuoc.controller; // Hoặc package phù hợp

import com.example.quanlicuahangthuoc.dto.StaffUpdateDTO;
import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;
import jakarta.validation.Valid;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Integer id,
                                         @Valid @RequestBody StaffUpdateDTO updateDTO) {
        try {
            Staff updatedStaff = staffService.updateStaff(id, updateDTO);
            return ResponseEntity.ok(updatedStaff);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi khi cập nhật nhân viên: " + e.getMessage());
        }
    }
}