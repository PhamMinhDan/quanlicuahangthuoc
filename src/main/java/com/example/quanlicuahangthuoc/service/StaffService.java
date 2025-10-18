package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    @Transactional
    public Staff addStaff(Staff staff) {
        // **Thêm logic kiểm tra nghiệp vụ ở đây** (ví dụ: email hoặc sđt đã tồn tại)
        if (staffRepository.findByEmail(staff.getEmail()).isPresent()) {
            throw new IllegalStateException("Email " + staff.getEmail() + " đã tồn tại.");
        }
        if (staffRepository.findByPhone(staff.getPhone()).isPresent()) {
            throw new IllegalStateException("Số điện thoại " + staff.getPhone() + " đã tồn tại.");
        }

        // Lưu nhân viên vào cơ sở dữ liệu
        return staffRepository.save(staff);
    }


    @Transactional
    public List<Staff> addAllStaff(List<Staff> staffList) {
        // Thêm các kiểm tra cần thiết cho từng nhân viên trong danh sách nếu cần
        for (Staff staff : staffList) {
            if (staffRepository.findByEmail(staff.getEmail()).isPresent()) {
                throw new IllegalStateException("Thêm thất bại: Email " + staff.getEmail() + " đã tồn tại.");
            }
            if (staffRepository.findByPhone(staff.getPhone()).isPresent()) {
                throw new IllegalStateException("Thêm thất bại: Số điện thoại " + staff.getPhone() + " đã tồn tại.");
            }
        }

        return staffRepository.saveAll(staffList);
    }
}