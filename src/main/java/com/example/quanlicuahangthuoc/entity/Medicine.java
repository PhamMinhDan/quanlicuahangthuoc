package com.example.quanlicuahangthuoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicine")
@Getter
@Setter // Đảm bảo @Setter được áp dụng
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String image;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineType type;

    @Size(max = 50, message = "Ngày hết hạn không được vượt quá 50 ký tự")
    @Column(name = "expiry_date")
    private String expiryDate;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Supplier supplier;

    //  Enum loại thuốc - khớp với dữ liệu trong database
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


    // Enum nhà cung cấp - khớp với dữ liệu trong database
    public enum Supplier {
        Pfizer("Pfizer"),
        Novartis("Novartis"),
        Johnson("Johnson"),
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