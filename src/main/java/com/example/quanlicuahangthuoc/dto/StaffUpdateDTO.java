package com.example.quanlicuahangthuoc.dto;

import com.example.quanlicuahangthuoc.entity.Staff.Role;
import com.example.quanlicuahangthuoc.entity.Staff.WorkShift;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
}