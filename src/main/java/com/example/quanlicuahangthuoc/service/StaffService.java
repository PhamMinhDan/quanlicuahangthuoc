package com.example.quanlicuahangthuoc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.repository.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;

    // Lấy tất cả nhân viên với phân trang và sắp xếp
    public Page<Staff> getAllStaff(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return staffRepository.findAll(pageable);
    }

    // Tìm kiếm nhân viên theo tên
    public Page<Staff> searchStaffByName(String name, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return staffRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    // Lọc nhân viên theo chức vụ
    public Page<Staff> getStaffByRole(Staff.Role role, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return staffRepository.findByRole(role, pageable);
    }

    // Tìm kiếm nhân viên theo cả tên và chức vụ
    public Page<Staff> searchStaffByNameAndRole(String name, Staff.Role role, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return staffRepository.findByNameContainingIgnoreCaseAndRole(name, role, pageable);
    }
}