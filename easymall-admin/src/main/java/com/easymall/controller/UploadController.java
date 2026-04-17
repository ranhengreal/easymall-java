package com.easymall.controller;

import com.easymall.entity.result.Result;
import com.easymall.service.UploadService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    @Resource
    private UploadService uploadService;

    /**
     * 上传品牌Logo
     */
    @PostMapping("/brand")
    public Result<String> uploadBrandLogo(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "brand");
        return Result.success(url);
    }

    /**
     * 上传商品图片
     */
    @PostMapping("/product")
    public Result<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "product");
        return Result.success(url);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "avatar");
        return Result.success(url);
    }

    /**
     * 上传轮播图图片
     */
    @PostMapping("/banner")
    public Result<String> uploadBannerImage(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "banner");
        return Result.success(url);
    }
}