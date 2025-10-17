package com.example.quanlicuahangthuoc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(min = 2, max = 255, message = "Tên nhân viên phải từ 2 đến 255 ký tự")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Vai trò không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @NotNull(message = "Ca làm việc không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "work_shift", nullable = false)
    private WorkShift workShift;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Lương phải lớn hơn hoặc bằng 0")
    @Column(name = "salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải từ 10 đến 15 chữ số")
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;


    public enum Role {
        quan_ly("Quản lý"),
        nhan_vien("Nhân viên");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    public enum WorkShift {
        sang("Ca sáng"),
        chieu("Ca chiều");

        private final String displayName;

        WorkShift(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public WorkShift getWorkShift() {
        return workShift;
    }

    public void setWorkShift(WorkShift workShift) {
        this.workShift = workShift;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}