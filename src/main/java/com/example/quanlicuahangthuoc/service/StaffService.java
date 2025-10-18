package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public void deleteStaff(Integer id) {
        if (!staffRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id + " để xóa.");
        }

        staffRepository.deleteById(id);
    }
}