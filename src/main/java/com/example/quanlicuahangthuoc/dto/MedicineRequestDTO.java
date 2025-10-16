package com.example.quanlicuahangthuoc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import com.example.quanlicuahangthuoc.entity.Medicine;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineRequestDTO {

    private Integer id;

    @NotBlank(message = "Image URL cannot be empty")
    private String image;

    private MultipartFile imageFile;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Type cannot be empty")
    private String type;

    private LocalDate expiryDate;

    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity cannot be empty")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;

    @NotBlank(message = "Supplier cannot be empty")
    private String supplier;

    // Convert to Medicine entity
    public Medicine toEntity() {
        Medicine medicine = new Medicine();
        medicine.setId(this.id);
        medicine.setImage(this.image);
        medicine.setName(this.name);
        medicine.setType(this.type);
        medicine.setExpiryDate(this.expiryDate);
        medicine.setPrice(this.price);
        medicine.setStockQuantity(this.stockQuantity);
        medicine.setSupplier(this.supplier);
        return medicine;
    }

    // Create from Medicine entity
    public static MedicineRequestDTO fromEntity(Medicine medicine) {
        MedicineRequestDTO dto = new MedicineRequestDTO();
        dto.setId(medicine.getId());
        dto.setImage(medicine.getImage());
        dto.setName(medicine.getName());
        dto.setType(medicine.getType());
        dto.setExpiryDate(medicine.getExpiryDate());
        dto.setPrice(medicine.getPrice());
        dto.setStockQuantity(medicine.getStockQuantity());
        dto.setSupplier(medicine.getSupplier());
        return dto;
    }

    // Thêm getter thủ công (dự phòng)
    public Integer getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getSupplier() {
        return supplier;
    }

    // Thêm setter thủ công (dự phòng)
    public void setId(Integer id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}