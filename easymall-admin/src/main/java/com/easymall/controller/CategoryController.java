package com.easymall.controller;

import com.easymall.entity.dto.CategoryDTO;
import com.easymall.entity.po.Category;
import com.easymall.entity.result.Result;
import com.easymall.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    // ==================== 查询接口 ====================

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryDTO.Response>> getTree() {
        List<Category> tree = categoryService.getTreeList();
        List<CategoryDTO.Response> response = CategoryDTO.Response.fromPOList(tree);
        return Result.success(response);
    }

    /**
     * 获取分类列表（平铺）
     */
    @GetMapping("/list")
    public Result<List<CategoryDTO.SimpleResponse>> getList() {
        List<Category> list = categoryService.getList();
        List<CategoryDTO.SimpleResponse> response = CategoryDTO.SimpleResponse.fromPOList(list);
        return Result.success(response);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    public Result<CategoryDTO.SimpleResponse> getById(@PathVariable String categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }
        return Result.success(CategoryDTO.SimpleResponse.fromPO(category));
    }

    /**
     * 获取分类路径
     */
    @GetMapping("/{categoryId}/path")
    public Result<CategoryDTO.PathResponse> getPath(@PathVariable String categoryId) {
        CategoryDTO.PathResponse path = categoryService.getCategoryPath(categoryId);
        if (path == null) {
            return Result.error(404, "分类不存在");
        }
        return Result.success(path);
    }

    // ==================== 增删改接口 ====================

    /**
     * 新增分类
     */
    @PostMapping
    public Result<String> add(@Valid @RequestBody CategoryDTO.Add dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setPCategoryId(dto.getPCategoryId());
        category.setSort(dto.getSort());

        boolean success = categoryService.add(category);
        if (success) {
            log.info("新增分类成功: {}", dto.getCategoryName());
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Result<String> update(@Valid @RequestBody CategoryDTO.Update dto) {
        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        category.setCategoryName(dto.getCategoryName());
        category.setPCategoryId(dto.getPCategoryId());
        category.setSort(dto.getSort());

        boolean success = categoryService.update(category);
        if (success) {
            log.info("更新分类成功: {}", dto.getCategoryName());
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public Result<String> delete(@PathVariable String categoryId) {
        boolean success = categoryService.delete(categoryId);
        if (success) {
            log.info("删除分类成功: {}", categoryId);
            return Result.success("删除成功");
        }
        return Result.error("请先删除子分类");
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@Valid @RequestBody CategoryDTO.BatchDelete dto) {
        categoryService.batchDelete(dto.getCategoryIds());
        log.info("批量删除成功，共{}条", dto.getCategoryIds().size());
        return Result.success("批量删除成功");
    }

    // ==================== 排序接口 ====================

    /**
     * 批量更新排序
     */
    @PutMapping("/sort/batch")
    public Result<String> batchUpdateSort(@Valid @RequestBody List<CategoryDTO.Sort> sortList) {
        categoryService.batchUpdateSort(sortList);
        return Result.success("排序更新成功");
    }

    /**
     * 移动分类
     */
    @PutMapping("/move")
    public Result<String> move(@Valid @RequestBody CategoryDTO.Move dto) {
        categoryService.moveCategory(dto);
        return Result.success("移动成功");
    }
}