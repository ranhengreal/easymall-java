package com.easymall.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    /**
     * 上传图片
     * @param file 图片文件
     * @param module 模块名称（brand, product, avatar等）
     * @return 图片访问路径
     */
    String uploadImage(MultipartFile file, String module);
}