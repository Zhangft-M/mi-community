package org.mi.api.post.vo;

import lombok.Data;
import org.mi.api.post.entity.EsPost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: mi-community
 * @description: 帖子页面展示数据实体
 * @author: Micah
 * @create: 2020-11-09 23:04
 **/
@Data
public class PostVO implements Serializable {

    private static final long serialVersionUID = 5404666359068603233L;

    private Long categoryId;

    private String categoryName;

    private Integer total;

    private List<EsPost> postDatas = new ArrayList<>();
}
