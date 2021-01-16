package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.dto.CategoryDTO;
import org.mi.biz.post.service.ICategoryService;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 16:04
 **/
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @Anonymous
    @GetMapping ("all")
    public R<List<CategoryDTO>> listData(){
        List<CategoryDTO> result = this.categoryService.listData();
        return R.success(result);
    }

}
