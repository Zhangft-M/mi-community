package org.mi.api.tool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

import java.io.Serializable;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 17:53
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mi_check")
public class Checker extends BaseEntity<Checker> implements Serializable {

    private static final long serialVersionUID = 439973295301242378L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String content;

    private String url;

    /**
     * 0 为文本 1为图片
     */
    private Integer type;

    private Boolean status;

}
