package com.easymall.controller;

import com.easymall.entity.result.Result;
import com.easymall.service.UploadService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Resource
    private UploadService uploadService;

    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "avatar");
        return Result.success(url);
    }

    /**
     * 上传评价图片
     */
    @PostMapping("/review")
    public Result<String> uploadReviewImage(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "review");
        return Result.success(url);
    }
}