package com.easymall.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class ImageController {

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/images/brand/{date}/{filename}")
    public void getBrandImage(
            @PathVariable String date,
            @PathVariable String filename,
            HttpServletResponse response) {

        // 使用 File.separator 确保路径分隔符正确
        String filePath = uploadPath + File.separator + "images" + File.separator + "brand" + File.separator + date + File.separator + filename;

        // 或者替换所有斜杠
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);

        File file = new File(filePath);

        System.out.println("请求图片路径: " + filePath);
        System.out.println("文件是否存在: " + file.exists());

        if (!file.exists()) {
            response.setStatus(404);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {

            String contentType = Files.probeContentType(Paths.get(filePath));
            if (contentType == null) {
                contentType = "image/jpeg";
            }
            response.setContentType(contentType);

            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();

        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}