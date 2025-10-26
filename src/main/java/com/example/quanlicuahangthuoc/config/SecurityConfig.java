package com.example.quanlicuahangthuoc.config;

import com.example.quanlicuahangthuoc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + username));
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/access-denied");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho đơn giản (cân nhắc bật lại nếu cần bảo mật cao)
                .authorizeHttpRequests(authz -> authz
                        // Cho phép truy cập công khai vào trang đăng nhập, từ chối truy cập và dashboard
                        .requestMatchers("/", "/login", "/access-denied").permitAll()
                        // Chỉ quản lý mới được thêm, sửa, xóa trên các trang quản lý (trừ đơn hàng)
                        .requestMatchers("/medicines/add", "/medicines/edit/**", "/medicines/delete/**").hasRole("quan_ly")
                        .requestMatchers("/customers/add", "/customers/edit/**").hasAnyRole("quan_ly", "nhan_vien")
                        .requestMatchers("/medicines/delete/**").hasRole("quan_ly")
                        .requestMatchers("/payments/new", "/payments/edit/**", "/payments/delete/**").hasRole("quan_ly")
                        .requestMatchers("/promotions/new", "/promotions/edit/**", "/promotions/delete/**").hasRole("quan_ly")
                        .requestMatchers("/api/staff/add", "/api/staff/edit/**", "/api/staff/delete/**").hasRole("quan_ly")
                        // Chỉ quản lý mới được xóa đơn hàng
                        .requestMatchers("/orders/delete/**").hasRole("quan_ly")
                        .requestMatchers("/dashboard").hasRole("quan_ly")
                        // Cả quản lý và nhân viên được xem, tìm kiếm, thêm và sửa đơn hàng
                        .requestMatchers("/orders/**", "/medicines/**", "/customers/**", "/payments/**", "/promotions/**").hasAnyRole("quan_ly", "nhan_vien")
                        // Các yêu cầu khác cần xác thực
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_quan_ly"))) {
                                response.sendRedirect("/dashboard");
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_nhan_vien"))) {
                                response.sendRedirect("/medicines/view-medicine");
                            } else {
                                response.sendRedirect("/access-denied");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler())
                );
        return http.build();
    }
}