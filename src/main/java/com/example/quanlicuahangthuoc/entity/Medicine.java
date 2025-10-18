package com.example.quanlicuahangthuoc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "medicine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String image;

    @NotBlank(message = "Tên thuốc không được để trống")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Loại thuốc không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineType type;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải là thời gian trong tương lai")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @NotNull(message = "Giá thuốc không được để trống")
    @Positive(message = "Giá thuốc phải lớn hơn 0")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @PositiveOrZero(message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @NotNull(message = "Nhà cung cấp không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Supplier supplier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MedicineType getType() {
        return type;
    }

    public void setType(MedicineType type) {
        this.type = type;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public enum MedicineType {
        giam_dau("Giảm đau"),
        khang_sinh("Kháng sinh"),
        chong_viem("Chống viêm"),
        thuoc_ha_huyet_ap("Thuốc hạ huyết áp");

        private final String displayName;

        MedicineType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Supplier {
        Pfizer("Pfizer"),     // Constant name: PFIZER
        Novartis("Novartis"), // Constant name: NOVARTIS
        Johnson("Johnson"),   // Constant name: JOHNSON
        Roche("Roche"),
        Merck("Merck"),
        Sanofi("Sanofi");

        private final String displayName;

        Supplier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}