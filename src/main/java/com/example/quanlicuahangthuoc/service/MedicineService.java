package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;

@Service

public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;
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

    // Tìm kiếm theo tên thuốc
    public List<Medicine> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllMedicines();
        }
        return medicineRepository.findByNameContainingIgnoreCase(name.trim());
    }

    // Tìm kiếm theo loại thuốc
    public List<Medicine> searchByType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return getAllMedicines();
        }
        
        try {
            Medicine.MedicineType type = Medicine.MedicineType.valueOf(typeStr.trim());
            return medicineRepository.findByType(type);
        } catch (IllegalArgumentException e) {
            return getAllMedicines(); // Trả về tất cả nếu enum không hợp lệ
        }
    }

    // Tìm kiếm theo nhà cung cấp
    public List<Medicine> searchBySupplier(String supplierStr) {
        if (supplierStr == null || supplierStr.trim().isEmpty()) {
            return getAllMedicines();
        }
        
        try {
            Medicine.Supplier supplier = Medicine.Supplier.valueOf(supplierStr.trim());
            return medicineRepository.findBySupplier(supplier);
        } catch (IllegalArgumentException e) {
            return getAllMedicines(); // Trả về tất cả nếu enum không hợp lệ
        }
    }

    // Tìm kiếm theo nhiều bộ lọc
    public List<Medicine> searchByFilters(String name, String typeStr, String supplierStr) {
        Medicine.MedicineType type = null;
        Medicine.Supplier supplier = null;
        
        // Parse type enum
        if (typeStr != null && !typeStr.trim().isEmpty()) {
            try {
                type = Medicine.MedicineType.valueOf(typeStr.trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid type
            }
        }
        
        // Parse supplier enum
        if (supplierStr != null && !supplierStr.trim().isEmpty()) {
            try {
                supplier = Medicine.Supplier.valueOf(supplierStr.trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid supplier
            }
        }
        
        return medicineRepository.findByFilters(
            (name != null && !name.trim().isEmpty()) ? name.trim() : null,
            type,
            supplier
        );
    }

    // Thêm mới thuốc
    public Medicine createMedicine(Medicine medicine) {
        // Validate dữ liệu đầu vào
        if (medicine.getName() == null || medicine.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        if (medicine.getType() == null) {
            throw new IllegalArgumentException("Loại thuốc không được để trống");
        }
        if (medicine.getSupplier() == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
        if (medicine.getPrice() == null || medicine.getPrice() < 0) {
            throw new IllegalArgumentException("Giá thuốc phải lớn hơn hoặc bằng 0");
        }
        if (medicine.getStockQuantity() == null || medicine.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho phải lớn hơn hoặc bằng 0");
        }

        // Set default values nếu chưa có
        if (medicine.getImage() == null || medicine.getImage().trim().isEmpty()) {
            medicine.setImage("default-medicine");
        }

        // Lưu vào database
        return medicineRepository.save(medicine);
    }
    // Xóa thuốc theo id
     public void deleteMedicine(Integer id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với id: " + id));

        medicineRepository.deleteById(id);
    }
    // Cập nhật thông tin thuốc
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

