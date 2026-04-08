package com.easymall.service.impl;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.BrandDTO;
import com.easymall.entity.po.Brand;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.BrandMapper;
import com.easymall.redis.RedisUtils;
import com.easymall.service.BrandService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private RedisUtils redisUtils;

    // ==================== 查询实现 ====================

    @Override
    public List<Brand> getList() {
        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<Brand> cached = (List<Brand>) redisUtils.get(Constants.REDIS_KEY_BRAND_LIST);
        if (cached != null && !cached.isEmpty()) {
            log.debug("从缓存获取品牌列表");
            return cached;
        }

        log.debug("缓存未命中，从数据库查询");
        List<Brand> list = brandMapper.selectAll();

        // 存入缓存
        if (list != null && !list.isEmpty()) {
            redisUtils.setex(Constants.REDIS_KEY_BRAND_LIST, list, Constants.REDIS_KEY_EXPIRE_HOUR);
        }

        return list != null ? list : List.of();
    }

    @Override
    public List<Brand> getEnabledList() {
        return brandMapper.selectEnabled();
    }

    @Override
    public Brand getById(String brandId) {
        String cacheKey = Constants.REDIS_KEY_BRAND + brandId;
        Brand cached = (Brand) redisUtils.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Brand brand = brandMapper.selectById(brandId);
        if (brand != null) {
            redisUtils.setex(cacheKey, brand, Constants.REDIS_KEY_EXPIRE_HOUR);
        }
        return brand;
    }

    // ==================== 增删改实现 ====================

    @Override
    @Transactional
    public boolean add(Brand brand) {
        // 生成ID
        if (!StringUtils.hasText(brand.getBrandId())) {
            brand.setBrandId(generateBrandId());
        }

        // 校验名称是否重复
        validateBrandName(brand);

        int result = brandMapper.insert(brand);

        if (result > 0) {
            clearCache();
            log.info("新增品牌成功: {}", brand.getBrandName());
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean update(Brand brand) {
        // 检查是否存在
        Brand existing = brandMapper.selectById(brand.getBrandId());
        if (existing == null) {
            throw new BusinessException("品牌不存在");
        }

        // 校验名称是否重复（排除自身）
        validateBrandNameForUpdate(brand);

        int result = brandMapper.update(brand);

        if (result > 0) {
            clearCache();
            clearBrandCache(brand.getBrandId());
            log.info("更新品牌成功: {} -> {}", brand.getBrandId(), brand.getBrandName());
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean delete(String brandId) {
        // 检查是否存在
        Brand existing = brandMapper.selectById(brandId);
        if (existing == null) {
            throw new BusinessException("品牌不存在");
        }

        int result = brandMapper.deleteById(brandId);

        if (result > 0) {
            clearCache();
            clearBrandCache(brandId);
            log.info("删除品牌成功: {}", brandId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public void batchDelete(List<String> brandIds) {
        if (brandIds == null || brandIds.isEmpty()) {
            return;
        }

        int count = brandMapper.batchDelete(brandIds);
        clearCache();

        // 清除单个品牌缓存
        for (String brandId : brandIds) {
            clearBrandCache(brandId);
        }

        log.info("批量删除品牌成功，共{}条", count);
    }

    // ==================== 排序实现 ====================

    @Override
    @Transactional
    public void batchUpdateSort(List<BrandDTO.Sort> sortList) {
        for (BrandDTO.Sort dto : sortList) {
            Brand brand = new Brand();
            brand.setBrandId(dto.getBrandId());
            brand.setSort(dto.getSort());
            brandMapper.updateSort(brand);
        }

        clearCache();
        log.info("批量更新排序完成，共{}条", sortList.size());
    }

    // ==================== 辅助方法 ====================

    /**
     * 新增时校验品牌名称是否重复
     */
    private void validateBrandName(Brand brand) {
        Brand existing = brandMapper.selectByName(brand.getBrandName());
        if (existing != null) {
            throw new BusinessException("品牌名称已存在");
        }
    }

    /**
     * 更新时校验品牌名称是否重复（排除自身）
     */
    private void validateBrandNameForUpdate(Brand brand) {
        Brand existing = brandMapper.selectByName(brand.getBrandName());
        if (existing != null && !existing.getBrandId().equals(brand.getBrandId())) {
            throw new BusinessException("品牌名称已存在");
        }
    }

    /**
     * 生成品牌ID（例如：B001, B002...）
     */
    private String generateBrandId() {
        String maxId = brandMapper.getMaxBrandId();
        if (maxId == null) {
            return Constants.BRAND_ID_PREFIX + "001";
        }

        try {
            int num = Integer.parseInt(maxId.substring(1));
            return String.format(Constants.BRAND_ID_PREFIX + "%03d", num + 1);
        } catch (NumberFormatException e) {
            log.warn("解析品牌ID失败: {}", maxId);
            return Constants.BRAND_ID_PREFIX + "001";
        }
    }

    /**
     * 清除品牌列表缓存
     */
    private void clearCache() {
        redisUtils.delete(Constants.REDIS_KEY_BRAND_LIST);
        log.debug("清除品牌列表缓存");
    }

    /**
     * 清除单个品牌缓存
     */
    private void clearBrandCache(String brandId) {
        String cacheKey = Constants.REDIS_KEY_BRAND + brandId;
        redisUtils.delete(cacheKey);
        log.debug("清除品牌缓存: {}", cacheKey);
    }
}