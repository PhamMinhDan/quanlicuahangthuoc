package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    // Lấy thuốc theo ID
    public Medicine getMedicineById(Integer id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy thuốc với ID: " + id));
    }

    // Lấy tất cả thuốc
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    // Lấy danh sách thuốc phân trang và sắp xếp
    public Page<Medicine> getAllMedicinesPaginated(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return medicineRepository.findAll(pageable);
    }

    // Tìm kiếm theo bộ lọc
    public List<Medicine> searchByFilters(String name, String type, String supplier) {
        return medicineRepository.findAll().stream()
                .filter(medicine -> (name == null || medicine.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(medicine -> (type == null || medicine.getType().name().equalsIgnoreCase(type)))
                .filter(medicine -> (supplier == null || medicine.getSupplier().name().equalsIgnoreCase(supplier)))
                .collect(Collectors.toList());
    }

    // Thêm mới thuốc
    @Transactional
    public void createMedicine(Medicine medicine) {
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
        if (medicineRepository.findByName(medicine.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên thuốc " + medicine.getName() + " đã tồn tại.");
        }
        if (medicine.getImage() == null || medicine.getImage().trim().isEmpty()) {
            medicine.setImage("/images/default-medicine.jpg");
        }
        medicineRepository.save(medicine);
    }

    // Cập nhật thuốc
    @Transactional
    public void updateMedicine(Integer id, Medicine updatedMedicine) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy thuốc với ID: " + id));

        if (updatedMedicine.getName() == null || updatedMedicine.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        if (updatedMedicine.getType() == null) {
            throw new IllegalArgumentException("Loại thuốc không được để trống");
        }
        if (updatedMedicine.getSupplier() == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
        if (updatedMedicine.getPrice() == null || updatedMedicine.getPrice() < 0) {
            throw new IllegalArgumentException("Giá thuốc phải lớn hơn hoặc bằng 0");
        }
        if (updatedMedicine.getStockQuantity() == null || updatedMedicine.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho phải lớn hơn hoặc bằng 0");
        }
        if (medicineRepository.findByName(updatedMedicine.getName()).isPresent() &&
                !medicineRepository.findByName(updatedMedicine.getName()).get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên thuốc " + updatedMedicine.getName() + " đã tồn tại.");
        }

        existingMedicine.setName(updatedMedicine.getName());
        existingMedicine.setType(updatedMedicine.getType());
        existingMedicine.setExpiryDate(updatedMedicine.getExpiryDate());
        existingMedicine.setPrice(updatedMedicine.getPrice());
        existingMedicine.setStockQuantity(updatedMedicine.getStockQuantity());
        existingMedicine.setSupplier(updatedMedicine.getSupplier());
        if (updatedMedicine.getImage() != null && !updatedMedicine.getImage().trim().isEmpty()) {
            existingMedicine.setImage(updatedMedicine.getImage());
        }

        medicineRepository.save(existingMedicine);
    }

    // Xóa thuốc
    @Transactional
    public void deleteMedicine(Integer id) {
        if (!medicineRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy thuốc với ID: " + id);
        }
        medicineRepository.deleteById(id);
    }
}