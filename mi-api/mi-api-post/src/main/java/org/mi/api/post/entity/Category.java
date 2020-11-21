package org.mi.api.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description: 类别实体类
 * @author: Micah
 * @create: 2020-11-09 23:12
 **/

@Data
@TableName("mi_category")
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity<Category> {

    private static final long serialVersionUID = 7664586717196017967L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String contentDescribe;

    private String icon;

    private String iconClass;

    private Boolean status;

    private Long postCount;

    private Integer orderNum;

}
