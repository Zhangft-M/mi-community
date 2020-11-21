package org.mi.post.test;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.mi.api.post.dto.CategoryDTO;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.api.post.vo.CommentTree;
import org.mi.biz.post.MiPostApplication;
import org.mi.biz.post.service.ICategoryService;
import org.mi.biz.post.service.ICommentService;
import org.mi.biz.post.service.IPostService;
import org.mi.common.core.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-14 17:55
 **/
@SpringBootTest(classes = MiPostApplication.class)
public class QueryData {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IPostService postService;

    @Autowired
    private ICommentService commentService;

    @Test
    public void queryData(){
        /*Iterable<EsPost> all = this.postDAO.findAll();
        all.forEach(System.out::println);*/
    }

    @Test
    public void deleteData(){
        Post postEntity = new Post();
        postEntity.setId(525030599455682560L);
        System.out.println(postEntity.deleteById());
    }

    @Test
    public void filterSearch(){
        PostQueryCriteria queryCriteria = new PostQueryCriteria();
        queryCriteria.setEnding(false);
        queryCriteria.setKeyword("烦死可惜");
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageResult list = this.postService.list(queryCriteria, pageRequest);
        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    public void queryCategoryData(){
        List<CategoryDTO> categoryDTOS = this.categoryService.listData();
        categoryDTOS.forEach(System.out::println);
    }

    @Test
    public void queryCommentData(){
        List<CommentTree> list = this.commentService.list(526541970320142336L);
        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    public void queryPostDataById(){
        EsPost dataById = this.postService.getDataById(526541970320142336L);
        System.out.println(dataById);
    }
}
