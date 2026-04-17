package com.easymall.controller;

import com.easymall.entity.dto.BannerDTO;
import com.easymall.entity.po.Banner;
import com.easymall.entity.result.Result;
import com.easymall.service.BannerService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/banner")
@Slf4j
public class BannerController {

    @Resource
    private BannerService bannerService;

    @GetMapping("/list")
    public Result<List<BannerDTO.Response>> getList() {
        List<Banner> list = bannerService.getList();
        List<BannerDTO.Response> response = list.stream()
                .map(BannerDTO.Response::fromPO)
                .collect(Collectors.toList());
        return Result.success(response);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody BannerDTO.Add dto) {
        Banner banner = new Banner();
        banner.setTitle(dto.getTitle());
        banner.setImageUrl(dto.getImageUrl());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setSort(dto.getSort() != null ? dto.getSort() : 0);
        banner.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        boolean success = bannerService.add(banner);
        if (success) {
            log.info("添加轮播图成功: {}", dto.getTitle());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody BannerDTO.Update dto) {
        Banner banner = new Banner();
        banner.setId(dto.getId());
        banner.setTitle(dto.getTitle());
        banner.setImageUrl(dto.getImageUrl());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setSort(dto.getSort());
        banner.setStatus(dto.getStatus());

        boolean success = bannerService.update(banner);
        if (success) {
            log.info("更新轮播图成功: id={}", dto.getId());
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        boolean success = bannerService.delete(id);
        if (success) {
            log.info("删除轮播图成功: id={}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @PutMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        boolean success = bannerService.updateStatus(id, status);
        if (success) {
            return Result.success(status == 1 ? "已启用" : "已禁用");
        }
        return Result.error("操作失败");
    }
}