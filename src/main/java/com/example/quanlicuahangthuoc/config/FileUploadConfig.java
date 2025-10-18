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

    @Value("${file.upload-dir:uploads/list-medicines}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối của thư mục uploads
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        File uploadDirectory = uploadPath.toFile();

        // Tạo thư mục nếu chưa tồn tại
        if (!uploadDirectory.exists()) {
            boolean created = uploadDirectory.mkdirs();
            if (created) {
                System.out.println("✓ Thư mục upload được tạo: " + uploadPath);
            }
        }

        // Đăng ký resource handler cho uploaded files
        String absolutePath = uploadPath.toAbsolutePath().toString();
        String fileLocation = "file:///" + absolutePath.replace("\\", "/") + "/";
        
        System.out.println("✓ Cấu hình resource handler:");
        System.out.println("  - URL path: /uploads/**");
        System.out.println("  - File location: " + fileLocation);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fileLocation)
                .setCachePeriod(3600);

        // Đăng ký resource handler cho static files (images mặc định)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600);
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileNameWithoutExt = originalFilename != null ?
                originalFilename.substring(0, originalFilename.lastIndexOf(".")) : "file";
        String fileName = System.currentTimeMillis() + "_" + fileNameWithoutExt + fileExtension;

        // Lấy đường dẫn tuyệt đối
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        Path filePath = uploadPath.resolve(fileName);

        // Tạo thư mục chứa file nếu chưa có
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new IOException("Không thể tạo thư mục: " + uploadPath, e);
        }

        // Lưu file
        try {
            Files.write(filePath, file.getBytes());
            System.out.println("✓ File lưu thành công: " + filePath);
        } catch (IOException e) {
            throw new IOException("Lỗi khi lưu file: " + fileName, e);
        }

        // Trả về đường dẫn tương đối để lưu vào database
        // Đường dẫn phải khớp với cấu hình /uploads/** 
        // và file được lưu trong thư mục uploads/list-medicines/
        String returnPath = "/uploads/list-medicines/" + fileName;
        System.out.println("✓ Trả về đường dẫn: " + returnPath);
        return returnPath;
    }

    public String getUploadDir() {
        return Paths.get(uploadDir).toAbsolutePath().toString();
    }
}