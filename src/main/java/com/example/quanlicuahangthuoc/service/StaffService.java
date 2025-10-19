package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private StaffRepository staffRepository;

    // Phân trang với sắp xếp theo name hoặc salary
    public Page<Staff> getStaffPage(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
            Sort.Direction.DESC : Sort.Direction.ASC;

        // Chỉ cho phép sort theo name hoặc salary, mặc định theo id
        Sort sort;
        if ("name".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(direction, "name");
        } else if ("salary".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(direction, "salary");
        } else {
            sort = Sort.by(direction, "id");
        }

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