package org.mi.biz.post.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.post.dto.CategoryDTO;
import org.mi.api.post.entity.Category;
import org.mi.api.post.mapstruct.CategoryMapStruct;
import org.mi.biz.post.mapper.CategoryMapper;
import org.mi.biz.post.service.ICategoryService;
import org.mi.common.core.constant.RedisCacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 16:47
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapStruct categoryMapStruct;

    @Override
    @Cacheable(value = RedisCacheConstant.POST_CATEGORIES_CACHE_PREFIX,unless = "#result == null")
    public List<CategoryDTO> listData() {
        List<Category> categories = this.baseMapper.selectList(Wrappers.<Category>lambdaQuery()
                .eq(Category::getStatus, true));
        List<Category> list = categories.stream().sorted(Comparator.comparingInt(Category::getOrderNum)).collect(Collectors.toList());
        return this.categoryMapStruct.toDto(list);
    }
}
