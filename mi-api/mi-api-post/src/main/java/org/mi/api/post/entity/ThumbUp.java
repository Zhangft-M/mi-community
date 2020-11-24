package org.mi.api.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 13:59
 **/
@Data
@TableName("mi_thumb_up")
@EqualsAndHashCode(callSuper = true)
public class ThumbUp extends BaseEntity<ThumbUp> {

    private static final long serialVersionUID = -8962734536325397764L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long contentId;

    @TableField(exist = false)
    private Integer type;

    private Boolean hasDelete;
}
