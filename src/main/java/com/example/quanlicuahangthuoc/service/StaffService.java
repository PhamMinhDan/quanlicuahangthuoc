package com.example.quanlicuahangthuoc.service; // Hoặc package phù hợp

import com.example.quanlicuahangthuoc.dto.StaffUpdateDTO;
import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public Staff updateStaff(Integer id, StaffUpdateDTO updateDTO) {
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id));

        existingStaff.setName(updateDTO.getName());
        existingStaff.setRole(updateDTO.getRole());
        existingStaff.setWorkShift(updateDTO.getWorkShift());
        existingStaff.setSalary(updateDTO.getSalary());
        existingStaff.setEmail(updateDTO.getEmail());
        existingStaff.setPhone(updateDTO.getPhone());

        return staffRepository.save(existingStaff);
    }
}