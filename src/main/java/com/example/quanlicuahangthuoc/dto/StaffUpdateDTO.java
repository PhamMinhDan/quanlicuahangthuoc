package com.example.quanlicuahangthuoc.dto; // Hoặc package phù hợp

import com.example.quanlicuahangthuoc.entity.Staff.Role;
import com.example.quanlicuahangthuoc.entity.Staff.WorkShift;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Giữ lại các annotation Lombok phòng trường hợp nó hoạt động trở lại
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffUpdateDTO {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(min = 2, max = 255, message = "Tên nhân viên phải từ 2 đến 255 ký tự")
    private String name;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    @NotNull(message = "Ca làm việc không được để trống")
    private WorkShift workShift;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Lương phải lớn hơn hoặc bằng 0")
    private BigDecimal salary;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải từ 10 đến 15 chữ số")
    private String phone;

    // =======================================================
    // BỔ SUNG GETTERS/SETTERS THỦ CÔNG ĐỂ KHẮC PHỤC LỖI LOMBOK
    // =======================================================

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