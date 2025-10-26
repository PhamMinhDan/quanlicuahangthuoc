package com.example.quanlicuahangthuoc.entity;

import com.example.quanlicuahangthuoc.entity.Staff;
import com.example.quanlicuahangthuoc.entity.User;
import com.example.quanlicuahangthuoc.repository.StaffRepository;
import com.example.quanlicuahangthuoc.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Lấy bản ghi staff có sẵn
            Staff managerStaff = staffRepository.findById(1)
                    .orElseThrow(() -> new IllegalStateException("Staff with ID 1 not found"));
            Staff employeeStaff = staffRepository.findById(2)
                    .orElseThrow(() -> new IllegalStateException("Staff with ID 2 not found"));

            // Thêm tài khoản vào bảng users nếu chưa tồn tại
            if (!userRepository.findByEmail("admin@gmail.com").isPresent()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123@"));
                admin.setRole(User.Role.quan_ly);
                admin.setStaff(managerStaff);
                userRepository.save(admin);
            }

            if (!userRepository.findByEmail("staff@gmail.com").isPresent()) {
                User staff = new User();
                staff.setName("Dan");
                staff.setEmail("staff@gmail.com");
                staff.setPassword(passwordEncoder.encode("dan123@"));
                staff.setRole(User.Role.nhan_vien);
                staff.setStaff(employeeStaff);
                userRepository.save(staff);
            }
        };
    }
}
