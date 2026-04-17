package com.easymall.controller;

import com.easymall.entity.dto.BannerDTO;
import com.easymall.entity.po.Banner;
import com.easymall.entity.result.Result;
import com.easymall.service.BannerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/banner")
public class BannerController {

    @Resource
    private BannerService bannerService;

    @GetMapping("/list")
    public Result<List<BannerDTO.Response>> getEnabledList() {
        List<Banner> list = bannerService.getEnabledList();
        List<BannerDTO.Response> response = list.stream()
                .map(BannerDTO.Response::fromPO)
                .collect(Collectors.toList());
        return Result.success(response);
    }
}