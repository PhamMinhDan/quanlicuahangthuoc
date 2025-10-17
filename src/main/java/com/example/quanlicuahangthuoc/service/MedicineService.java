package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    // Constructor thủ công thay thế @RequiredArgsConstructor
    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public void deleteMedicine(Integer id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với id: " + id));

        medicineRepository.deleteById(id);
    }
    public Medicine updateMedicine(Integer id, Medicine updatedMedicine) {

        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can not find id " + id));


        existingMedicine.setImage(updatedMedicine.getImage());
        existingMedicine.setName(updatedMedicine.getName());
        existingMedicine.setType(updatedMedicine.getType());
        existingMedicine.setExpiryDate(updatedMedicine.getExpiryDate());
        existingMedicine.setPrice(updatedMedicine.getPrice());
        existingMedicine.setStockQuantity(updatedMedicine.getStockQuantity());
        existingMedicine.setSupplier(updatedMedicine.getSupplier());


        return medicineRepository.save(existingMedicine);
    }

}