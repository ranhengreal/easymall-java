package com.easymall.controller;

import com.easymall.entity.dto.CategoryDTO;
import com.easymall.entity.po.Category;
import com.easymall.entity.result.Result;
import com.easymall.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")  // 实际路径：/api/category/tree
@Slf4j
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<CategoryDTO.Response>> getTree() {
        List<Category> tree = categoryService.getTreeList();
        List<CategoryDTO.Response> response = CategoryDTO.Response.fromPOList(tree);
        return Result.success(response);
    }
}