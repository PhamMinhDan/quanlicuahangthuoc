package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    // Lấy nhân viên theo ID
    public Staff getStaffById(Integer id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id));
    }

    public long countStaffByName(String name) {
        return staffRepository.countByNameContainingIgnoreCase(name);
    }

    public long countStaffByRole(Staff.Role role) {
        return staffRepository.countByRole(role);
    }

    public long countStaffByNameAndRole(String name, Staff.Role role) {
        return staffRepository.countByNameContainingIgnoreCaseAndRole(name, role);
    }
    // Phân trang với sắp xếp theo name hoặc salary
    public Page<Staff> getStaffPage(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

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

    @Transactional
    public Staff addStaff(Staff staff) {
        if (staffRepository.findByEmail(staff.getEmail()).isPresent()) {
            throw new IllegalStateException("Email " + staff.getEmail() + " đã tồn tại.");
        }
        if (staffRepository.findByPhone(staff.getPhone()).isPresent()) {
            throw new IllegalStateException("Số điện thoại " + staff.getPhone() + " đã tồn tại.");
        }
        return staffRepository.save(staff);
    }

    @Transactional
    public void deleteStaff(Integer id) {
        if (!staffRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id + " để xóa.");
        }
        staffRepository.deleteById(id);
    }

    @Transactional
    public Staff updateStaff(Staff staff) {
        if (!staffRepository.existsById(staff.getId())) {
            throw new NoSuchElementException("Không tìm thấy nhân viên với ID: " + staff.getId());
        }
        if (staffRepository.findByEmail(staff.getEmail()).isPresent() &&
                !staffRepository.findByEmail(staff.getEmail()).get().getId().equals(staff.getId())) {
            throw new IllegalStateException("Email " + staff.getEmail() + " đã tồn tại.");
        }
        if (staffRepository.findByPhone(staff.getPhone()).isPresent() &&
                !staffRepository.findByPhone(staff.getPhone()).get().getId().equals(staff.getId())) {
            throw new IllegalStateException("Số điện thoại " + staff.getPhone() + " đã tồn tại.");
        }
        return staffRepository.save(staff);
    }

    public long getTotalStaff() {
        return staffRepository.count();
    }

    public BigDecimal getTotalSalary() {
        List<Staff> staffList = staffRepository.findAll();
        return staffList.stream()
                .map(Staff::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTotalMorningShift() {
        return staffRepository.countByWorkShift(Staff.WorkShift.sang);
    }

    public long getTotalManagers() {
        return staffRepository.countByRole(Staff.Role.quan_ly);
    }
    public long getTotalStaff(String name, String role) {
        if ((name != null && !name.trim().isEmpty()) || (role != null && !role.trim().isEmpty())) {
            Staff.Role roleEnum = null;
            try {
                if (role != null && !role.isEmpty()) {
                    roleEnum = Staff.Role.valueOf(role);
                }
            } catch (IllegalArgumentException e) {
                roleEnum = null; // Bỏ qua nếu role không hợp lệ
            }
            return staffRepository.countByFilters(name, roleEnum);
        } else {
            return staffRepository.count();
        }
    }
}