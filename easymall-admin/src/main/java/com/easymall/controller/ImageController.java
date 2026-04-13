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

    /**
     * 获取品牌Logo
     */
    @GetMapping("/images/brand/{date}/{filename}")
    public void getBrandImage(
            @PathVariable String date,
            @PathVariable String filename,
            HttpServletResponse response) {

        String filePath = uploadPath + File.separator + "images" + File.separator + "brand" + File.separator + date + File.separator + filename;
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);
        writeImageToResponse(filePath, response);
    }

    /**
     * 获取商品图片
     */
    @GetMapping("/images/product/{date}/{filename}")
    public void getProductImage(
            @PathVariable String date,
            @PathVariable String filename,
            HttpServletResponse response) {

        String filePath = uploadPath + File.separator + "images" + File.separator + "product" + File.separator + date + File.separator + filename;
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);

        System.out.println("商品图片路径: " + filePath);
        System.out.println("文件是否存在: " + new File(filePath).exists());

        writeImageToResponse(filePath, response);
    }

    /**
     * 获取用户头像
     */
    @GetMapping("/images/avatar/{date}/{filename}")
    public void getAvatarImage(
            @PathVariable String date,
            @PathVariable String filename,
            HttpServletResponse response) {

        String filePath = uploadPath + File.separator + "images" + File.separator + "avatar" + File.separator + date + File.separator + filename;
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);
        writeImageToResponse(filePath, response);
    }

    /**
     * 通用图片响应方法
     */
    private void writeImageToResponse(String filePath, HttpServletResponse response) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
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
            response.setHeader("Cache-Control", "max-age=3600");

            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();

        } catch (Exception e) {
            System.out.println("写入图片失败: " + e.getMessage());
            response.setStatus(500);
        }
    }
}