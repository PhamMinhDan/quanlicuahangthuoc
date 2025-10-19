package com.example.quanlicuahangthuoc.controller; // Hoặc package phù hợp

import java.util.List;
import org.springframework.data.domain.Page;
import com.example.quanlicuahangthuoc.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/staff")
@Validated
public class StaffController {
    @Autowired
    private  StaffService staffService;

    @GetMapping("/list")
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
    @GetMapping("/search/role-name")
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
    @DeleteMapping("delete/{id}")
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
     @PutMapping("/update/{id}")
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
   
}