package com.example.quanlicuahangthuoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:../uploads/list_image/}") // Đường dẫn tuyệt đối hoặc tương đối từ root project
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tạo thư mục upload nếu chưa có
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir.replace("\\", "/")); // Đảm bảo định dạng đường dẫn
    }


    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);

        // Tạo thư mục chứa file nếu chưa có
        Files.createDirectories(path.getParent());

        // Lưu file
        Files.write(path, file.getBytes());

        // Trả về đường dẫn tương đối của file
        return "/list_image/" + fileName;
    }

    public String getUploadDir() {
        return uploadDir;
    }
}