package com.easymall.controller;

import com.easymall.entity.dto.BrandDTO;
import com.easymall.entity.po.Brand;
import com.easymall.entity.result.Result;
import com.easymall.service.BrandService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
@Slf4j
public class BrandController {

    @Resource
    private BrandService brandService;

    // ==================== 查询接口 ====================

    /**
     * 获取品牌列表
     * GET /brand
     */
    @GetMapping
    public Result<List<BrandDTO.Response>> getList() {
        List<Brand> list = brandService.getList();
        return Result.success(BrandDTO.Response.fromPOList(list));
    }

    /**
     * 获取启用的品牌列表（用于下拉选择）
     * GET /brand/enabled
     */
    @GetMapping("/enabled")
    public Result<List<BrandDTO.Response>> getEnabledList() {
        List<Brand> list = brandService.getEnabledList();
        return Result.success(BrandDTO.Response.fromPOList(list));
    }

    /**
     * 获取品牌详情
     * GET /brand/{brandId}
     */
    @GetMapping("/{brandId}")
    public Result<BrandDTO.Response> getById(@PathVariable String brandId) {
        Brand brand = brandService.getById(brandId);
        if (brand == null) {
            return Result.error(404, "品牌不存在");
        }
        return Result.success(BrandDTO.Response.fromPO(brand));
    }

    // ==================== 增删改接口 ====================

    /**
     * 新增品牌
     * POST /brand
     */
    @PostMapping
    public Result<String> add(@Valid @RequestBody BrandDTO.Add dto) {
        Brand brand = new Brand();
        brand.setBrandName(dto.getBrandName());
        brand.setBrandLogo(dto.getBrandLogo());
        brand.setDescription(dto.getDescription());
        brand.setSort(dto.getSort());
        brand.setStatus(dto.getStatus());

        boolean success = brandService.add(brand);
        if (success) {
            log.info("新增品牌成功: {}", dto.getBrandName());
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    /**
     * 更新品牌（包含状态更新）
     * PUT /brand/{brandId}
     */
    @PutMapping("/{brandId}")
    public Result<String> update(@PathVariable String brandId,
                                 @Valid @RequestBody BrandDTO.Update dto) {
        Brand brand = new Brand();
        brand.setBrandId(brandId);
        brand.setBrandName(dto.getBrandName());
        brand.setBrandLogo(dto.getBrandLogo());
        brand.setDescription(dto.getDescription());
        brand.setSort(dto.getSort());
        brand.setStatus(dto.getStatus());

        boolean success = brandService.update(brand);
        if (success) {
            log.info("更新品牌成功: {}", brandId);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除品牌
     * DELETE /brand/{brandId}
     */
    @DeleteMapping("/{brandId}")
    public Result<String> delete(@PathVariable String brandId) {
        boolean success = brandService.delete(brandId);
        if (success) {
            log.info("删除品牌成功: {}", brandId);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除品牌
     * DELETE /brand/batch
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@Valid @RequestBody BrandDTO.BatchDelete dto) {
        brandService.batchDelete(dto.getBrandIds());
        log.info("批量删除品牌成功，共{}条", dto.getBrandIds().size());
        return Result.success("批量删除成功");
    }

    // ==================== 排序接口 ====================

    /**
     * 批量更新排序
     * PUT /brand/sort/batch
     */
    @PutMapping("/sort/batch")
    public Result<String> batchUpdateSort(@Valid @RequestBody List<BrandDTO.Sort> sortList) {
        brandService.batchUpdateSort(sortList);
        return Result.success("排序更新成功");
    }
}