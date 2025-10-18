package com.example.quanlicuahangthuoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableJpaRepositories(basePackages = "com.example.quanlicuahangthuoc.repository")
@SpringBootApplication
public class QuanlicuahangthuocApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanlicuahangthuocApplication.class, args);
    }

}