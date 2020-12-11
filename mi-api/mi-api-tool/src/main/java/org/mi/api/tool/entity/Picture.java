package org.mi.api.tool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 14:13
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mi_picture")
public class Picture extends BaseEntity<Picture> {

    private static final long serialVersionUID = 8344105610734547155L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Boolean status;

    private Integer type;
}
