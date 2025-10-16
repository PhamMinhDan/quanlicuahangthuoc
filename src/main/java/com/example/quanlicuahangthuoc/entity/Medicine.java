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

    @NotBlank(message = "Image URL không thể bỏ trống")
    private String image;

    @NotBlank(message = "Tên không thể bỏ trống")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Loại thuốc là bắt buộc")
    private MedicineType type;

    @Size(max = 50, message = "Ngày hết hạn không được vượt quá 50 ký tự")
    @Column(name = "expiry_date")
    private String expiryDate;

    @NotNull(message = "Giá là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    private Double price;

    @NotNull(message = "Số lượng tồn kho là bắt buộc")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Nhà cung cấp là bắt buộc")
    private Supplier supplier;

    // ✅ Enum loại thuốc (đặt tên không dấu, dùng @JsonProperty nếu cần hiển thị tiếng Việt)
    public enum MedicineType {
        GIAM_DAU("Giảm đau"),
        KHANG_SINH("Kháng sinh"),
        CHONG_VIEM("Chống viêm"),
        THUOC_HA_HUYET_AP("Thuốc hạ huyết áp");

        private final String displayName;

        MedicineType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ✅ Enum nhà cung cấp
    public enum Supplier {
        PFIZER("Pfizer"),
        NOVARTIS("Novartis"),
        JOHNSON_AND_JOHNSON("Johnson & Johnson"),
        ROCHE("Roche"),
        MERCK_AND_CO("Merck & Co."),
        SANOFI("Sanofi");

        private final String displayName;

        Supplier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}