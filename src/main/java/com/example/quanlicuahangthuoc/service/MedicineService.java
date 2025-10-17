// MedicineService.java
package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    private Sort getSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        if ("name".equalsIgnoreCase(sortBy)) {
            return Sort.by(direction, "name");
        } else if ("price".equalsIgnoreCase(sortBy)) {
            return Sort.by(direction, "price");
        } else if ("id".equalsIgnoreCase(sortBy)) {
            return Sort.by(direction, "id");
        } else {
            // Mặc định sắp xếp theo ID
            return Sort.by(Sort.Direction.ASC, "id");
        }
    }

    public Page<Medicine> getAllMedicinesPaginated(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, getSort(sortBy, sortDirection));
        return medicineRepository.findAll(pageable);
    }

    public List<Medicine> getAllMedicinesSorted(String sortBy, String sortDirection) {
        return medicineRepository.findAll(getSort(sortBy, sortDirection));
    }

    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }
}
