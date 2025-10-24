//package com.example.quanlicuahangthuoc.entity;
//
//import com.example.quanlicuahangthuoc.entity.Staff;
//import com.example.quanlicuahangthuoc.entity.User;
//import com.example.quanlicuahangthuoc.repository.StaffRepository;
//import com.example.quanlicuahangthuoc.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class DataInitializer {
//
//    @Bean
//    public CommandLineRunner initData(UserRepository userRepository, StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            // Lấy bản ghi staff có sẵn
//            Staff managerStaff = staffRepository.findById(8)
//                    .orElseThrow(() -> new IllegalStateException("Staff with ID 8 not found"));
//            Staff employeeStaff = staffRepository.findById(9)
//                    .orElseThrow(() -> new IllegalStateException("Staff with ID 9 not found"));
//
//            // Thêm tài khoản vào bảng users nếu chưa tồn tại
//            if (!userRepository.findByEmail("new_admin4@example.com").isPresent()) {
//                User admin = new User();
//                admin.setName("new_admin4");
//                admin.setEmail("admin4@gmail.com");
//                admin.setPassword(passwordEncoder.encode("admin123@"));
//                admin.setRole(User.Role.quan_ly);
//                admin.setStaff(managerStaff);
//                userRepository.save(admin);
//            }
//
//            if (!userRepository.findByEmail("new_staff4@example.com").isPresent()) {
//                User staff = new User();
//                staff.setName("new_staff4");
//                staff.setEmail("staff4@example.com");
//                staff.setPassword(passwordEncoder.encode("staff123@"));
//                staff.setRole(User.Role.nhan_vien);
//                staff.setStaff(employeeStaff);
//                userRepository.save(staff);
//            }
//        };
//    }
//}