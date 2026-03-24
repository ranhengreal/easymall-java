package com.easymall.service.impl;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.CategoryDTO;
import com.easymall.entity.po.Category;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.CategoryMapper;
import com.easymall.redis.RedisUtils;
import com.easymall.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private RedisUtils redisUtils;

    // ==================== 查询实现 ====================

    @Override
    public List<Category> getTreeList() {
        // 1. 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<Category> cached = (List<Category>) redisUtils.get(Constants.REDIS_KEY_CATEGORY_TREE);
        if (cached != null && !cached.isEmpty()) {
            log.debug("从缓存获取分类树");
            return cached;
        }

        log.debug("缓存未命中，从数据库查询");

        // 2. 从数据库查询
        List<Category> all = categoryMapper.selectAll();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 构建树
        List<Category> tree = buildTree(all);

        // 4. 存入缓存
        redisUtils.setex(Constants.REDIS_KEY_CATEGORY_TREE, tree, Constants.REDIS_KEY_EXPIRE_HOUR);

        return tree;
    }

    /**
     * 构建树形结构（使用 Map 优化，O(n) 复杂度）
     */
    private List<Category> buildTree(List<Category> all) {
        // 构建父ID -> 子分类列表的映射
        Map<String, List<Category>> childrenMap = new HashMap<>();

        for (Category category : all) {
            String parentId = category.getPCategoryId();
            if (!StringUtils.hasText(parentId)) {
                parentId = Constants.CATEGORY_ROOT_PARENT_ID;
            }
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
        }

        // 找出顶级分类
        List<Category> roots = childrenMap.getOrDefault(Constants.CATEGORY_ROOT_PARENT_ID, new ArrayList<>());

        // 递归填充子分类
        for (Category root : roots) {
            fillChildren(root, childrenMap);
        }

        return roots;
    }

    /**
     * 递归填充子分类
     */
    private void fillChildren(Category parent, Map<String, List<Category>> childrenMap) {
        List<Category> children = childrenMap.getOrDefault(parent.getCategoryId(), new ArrayList<>());
        // 按排序值排序
        children.sort(Comparator.comparing(Category::getSort));
        parent.setChildren(children);
        for (Category child : children) {
            fillChildren(child, childrenMap);
        }
    }

    @Override
    public List<Category> getList() {
        List<Category> list = categoryMapper.selectAll();
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public Category getById(String categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public CategoryDTO.PathResponse getCategoryPath(String categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return null;
        }

        List<String> pathIds = new ArrayList<>();
        List<String> pathNames = new ArrayList<>();
        Category current = category;

        // 从当前分类向上追溯
        while (current != null && !Constants.CATEGORY_ROOT_PARENT_ID.equals(current.getPCategoryId())) {
            pathIds.add(0, current.getCategoryId());
            pathNames.add(0, current.getCategoryName());
            current = categoryMapper.selectById(current.getPCategoryId());
        }

        // 添加顶级分类
        if (current != null) {
            pathIds.add(0, current.getCategoryId());
            pathNames.add(0, current.getCategoryName());
        }

        String fullPath = String.join(" / ", pathNames);

        return CategoryDTO.PathResponse.of(categoryId, fullPath, pathIds, pathNames);
    }

    // ==================== 增删改实现 ====================

    @Override
    @Transactional
    public boolean add(Category category) {
        // 生成ID
        if (!StringUtils.hasText(category.getCategoryId())) {
            category.setCategoryId(generateCategoryId());
        }

        // 设置默认父ID
        if (!StringUtils.hasText(category.getPCategoryId())) {
            category.setPCategoryId(Constants.CATEGORY_ROOT_PARENT_ID);
        }

        // 设置默认排序
        if (category.getSort() == null) {
            category.setSort(Constants.CATEGORY_DEFAULT_SORT);
        }

        // 校验
        validateCategory(category);

        // 保存
        int result = categoryMapper.insert(category);

        // 清除缓存
        if (result > 0) {
            clearCache();
            log.info("新增分类成功: {}", category.getCategoryName());
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean update(Category category) {
        // 检查是否存在
        Category existing = categoryMapper.selectById(category.getCategoryId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }

        // 校验
        validateCategory(category);

        // 更新
        int result = categoryMapper.update(category);

        // 清除缓存
        if (result > 0) {
            clearCache();
            log.info("更新分类成功: {}", category.getCategoryName());
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean delete(String categoryId) {
        // 检查是否有子分类
        int childCount = categoryMapper.countChildren(categoryId);
        if (childCount > 0) {
            log.warn("删除失败，分类有子分类: {}", categoryId);
            return false;
        }

        // 删除
        int result = categoryMapper.deleteById(categoryId);

        // 清除缓存
        if (result > 0) {
            clearCache();
            log.info("删除分类成功: {}", categoryId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public void batchDelete(List<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        // 检查是否有子分类
        for (String categoryId : categoryIds) {
            int childCount = categoryMapper.countChildren(categoryId);
            if (childCount > 0) {
                throw new BusinessException("分类 " + categoryId + " 存在子分类，无法删除");
            }
        }

        // 批量删除
        int count = categoryMapper.batchDelete(categoryIds);

        // 清除缓存
        clearCache();
        log.info("批量删除分类成功，共{}条", count);
    }

    // ==================== 排序实现 ====================

    @Override
    @Transactional
    public void batchUpdateSort(List<CategoryDTO.Sort> sortList) {
        for (CategoryDTO.Sort dto : sortList) {
            Category category = new Category();
            category.setCategoryId(dto.getCategoryId());
            category.setSort(dto.getSort());
            categoryMapper.updateSort(category);
        }

        clearCache();
        log.info("批量更新排序完成，共{}条", sortList.size());
    }

    @Override
    @Transactional
    public void moveCategory(CategoryDTO.Move moveDTO) {
        String categoryId = moveDTO.getCategoryId();
        String targetId = moveDTO.getTargetCategoryId();
        String moveType = moveDTO.getMoveType();

        Category category = categoryMapper.selectById(categoryId);
        Category target = categoryMapper.selectById(targetId);

        if (category == null || target == null) {
            throw new BusinessException("分类不存在");
        }

        switch (moveType) {
            case "before":
                moveBefore(category, target);
                break;
            case "after":
                moveAfter(category, target);
                break;
            case "inner":
                moveInner(category, target);
                break;
            default:
                throw new BusinessException("移动类型不支持");
        }

        clearCache();
        log.info("移动分类成功: {} -> {}", categoryId, targetId);
    }

    private void moveBefore(Category category, Category target) {
        category.setPCategoryId(target.getPCategoryId());
        categoryMapper.updateParent(category);
        categoryMapper.shiftSortAfter(target.getPCategoryId(), target.getSort(), 1);
        category.setSort(target.getSort());
        categoryMapper.updateSort(category);
    }

    private void moveAfter(Category category, Category target) {
        category.setPCategoryId(target.getPCategoryId());
        categoryMapper.updateParent(category);
        categoryMapper.shiftSortAfter(target.getPCategoryId(), target.getSort(), 1);
        category.setSort(target.getSort() + 1);
        categoryMapper.updateSort(category);
    }

    private void moveInner(Category category, Category target) {
        category.setPCategoryId(target.getCategoryId());
        categoryMapper.updateParent(category);
        Integer maxSort = categoryMapper.getMaxSortByParent(target.getCategoryId());
        category.setSort((maxSort == null ? 0 : maxSort) + 1);
        categoryMapper.updateSort(category);
    }

    // ==================== 辅助方法 ====================

    /**
     * 校验分类
     */
    private void validateCategory(Category category) {
        // 检查名称重复
        Category existing = categoryMapper.selectByName(category.getCategoryName());
        if (existing != null && !existing.getCategoryId().equals(category.getCategoryId())) {
            throw new BusinessException("分类名称已存在");
        }

        // 检查父分类是否存在
        if (!Constants.CATEGORY_ROOT_PARENT_ID.equals(category.getPCategoryId())) {
            Category parent = categoryMapper.selectById(category.getPCategoryId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }

            // 检查层级深度
            int depth = getCategoryDepth(parent.getCategoryId());
            if (depth >= Constants.CATEGORY_MAX_DEPTH) {
                throw new BusinessException("分类层级不能超过" + Constants.CATEGORY_MAX_DEPTH + "级");
            }
        }
    }

    /**
     * 获取分类层级深度
     */
    private int getCategoryDepth(String categoryId) {
        int depth = 0;
        String currentId = categoryId;

        while (StringUtils.hasText(currentId) && !Constants.CATEGORY_ROOT_PARENT_ID.equals(currentId)) {
            depth++;
            Category category = categoryMapper.selectById(currentId);
            if (category == null) {
                break;
            }
            currentId = category.getPCategoryId();
        }

        return depth;
    }

    /**
     * 生成分类ID
     */
    private String generateCategoryId() {
        String maxId = categoryMapper.getMaxCategoryId();
        if (maxId == null) {
            return Constants.CATEGORY_ID_PREFIX + "001";
        }

        try {
            int num = Integer.parseInt(maxId.substring(1));
            return String.format(Constants.CATEGORY_ID_PREFIX + "%03d", num + 1);
        } catch (NumberFormatException e) {
            log.warn("解析分类ID失败: {}", maxId);
            return Constants.CATEGORY_ID_PREFIX + "001";
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        redisUtils.delete(Constants.REDIS_KEY_CATEGORY_TREE);
        log.debug("清除分类缓存");
    }
}