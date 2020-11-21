package org.mi.biz.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.post.dto.CategoryDTO;
import org.mi.api.post.entity.Category;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 16:06
 **/
public interface ICategoryService extends IService<Category> {
    /**
     * 查询所有的分类
     * @return
     */
    List<CategoryDTO> listData();
}
