package com.easymall.controller;

import com.easymall.entity.dto.BrandDTO;
import com.easymall.entity.po.Brand;
import com.easymall.entity.result.Result;
import com.easymall.service.BrandService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")  // 实际路径：/api/brand/enabled
@Slf4j
public class BrandController {

    @Resource
    private BrandService brandService;

    @GetMapping("/enabled")
    public Result<List<BrandDTO.Response>> getEnabledList() {
        List<Brand> list = brandService.getEnabledList();
        List<BrandDTO.Response> response = BrandDTO.Response.fromPOList(list);
        return Result.success(response);
    }
}