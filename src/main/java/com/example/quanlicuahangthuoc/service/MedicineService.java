package com.example.quanlicuahangthuoc.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.quanlicuahangthuoc.entity.Medicine;
import com.example.quanlicuahangthuoc.repository.MedicineRepository;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    @Value("${file.upload-dir:../uploads/list_image/}")
    private String uploadDir;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAllMedicines() {
        try {
            return medicineRepository.findAll();
        } catch (Exception e) {
            return List.of(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    public List<Medicine> searchByName(String name) {
        try {
            return name == null ? List.of() : medicineRepository.findByNameContainingIgnoreCase(name);
        } catch (Exception e) {
            return List.of(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    public List<Medicine> searchByType(String type) {
        try {
            return type == null ? List.of() : medicineRepository.findByTypeContainingIgnoreCase(type);
        } catch (Exception e) {
            return List.of(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    public List<Medicine> searchBySupplier(String supplier) {
        try {
            return supplier == null ? List.of() : medicineRepository.findBySupplierContainingIgnoreCase(supplier);
        } catch (Exception e) {
            return List.of(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    public List<Medicine> searchByFilters(String name, String type, String supplier) {
        try {
            return medicineRepository.findByFilters(name, type, supplier);
        } catch (Exception e) {
            return List.of(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    public void createMedicine(String name, String type, String expiryDate, double price, int stockQuantity, String supplier, MultipartFile image, String description) {
        try {
            // Validate dữ liệu
            if (name == null || name.trim().isEmpty() || name.length() > 30) {
                throw new IllegalArgumentException("Tên thuốc không hợp lệ (tối đa 30 ký tự).");
            }
            if (type == null || supplier == null) {
                throw new IllegalArgumentException("Loại và nhà cung cấp là bắt buộc.");
            }
            if (stockQuantity < 0) {
                throw new IllegalArgumentException("Số lượng tồn kho không được âm.");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Giá không được âm.");
            }
            if (description != null && description.length() > 100) {
                throw new IllegalArgumentException("Mô tả không vượt quá 100 ký tự.");
            }

            // Tạo đối tượng Medicine
            Medicine medicine = new Medicine();
            medicine.setName(name.trim());
            medicine.setType(type);
            medicine.setExpiryDate(LocalDate.parse(expiryDate));
            medicine.setPrice(BigDecimal.valueOf(price));
            medicine.setStockQuantity(stockQuantity);
            medicine.setSupplier(supplier);
            // medicine.setDescription(description); // Nếu thêm field description

            // Xử lý upload ảnh
            if (image != null && !image.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
                Path path = Paths.get(uploadDir + fileName);
                Files.createDirectories(path.getParent());
                image.transferTo(path.toFile());
                medicine.setImage(fileName);
            } else {
                throw new IllegalArgumentException("Hình ảnh là bắt buộc.");
            }

            // Lưu vào database
            medicineRepository.save(medicine);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw e; // Ném lại exception validate
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu thuốc: " + e.getMessage());
        }
    }
}