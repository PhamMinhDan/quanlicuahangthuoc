package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository; // Thay @RequiredArgsConstructor bằng @Autowired

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }
}