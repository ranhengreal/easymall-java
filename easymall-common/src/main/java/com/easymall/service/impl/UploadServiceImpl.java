package com.easymall.service.impl;

import com.easymall.exception.BusinessException;
import com.easymall.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String uploadImage(MultipartFile file, String module) {
        // 1. 校验文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文件类型，仅支持：JPEG、PNG、GIF、WebP");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过 5MB");
        }

        try {
            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 3. 按日期分类
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String savePath = uploadPath + "/images/" + module + "/" + dateDir;

            // 4. 创建目录
            File targetDir = new File(savePath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
                System.out.println("创建目录: " + savePath);
            }

            // 5. 保存文件
            File targetFile = new File(targetDir, filename);
            file.transferTo(targetFile);

            // 6. 返回访问URL（不包含 /api 前缀，ImageController 会处理）
            String url = "/images/" + module + "/" + dateDir + "/" + filename;

            System.out.println("文件保存成功: " + targetFile.getAbsolutePath());
            System.out.println("访问URL: " + url);

            return url;

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }
}