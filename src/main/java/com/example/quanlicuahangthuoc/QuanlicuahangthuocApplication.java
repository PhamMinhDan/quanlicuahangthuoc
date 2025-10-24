package com.example.quanlicuahangthuoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.quanlicuahangthuoc.repository")
@ComponentScan(basePackages = {"com.example.quanlicuahangthuoc", "com.example.quanlicuahangthuoc.config"})
public class QuanlicuahangthuocApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanlicuahangthuocApplication.class, args);
    }

}