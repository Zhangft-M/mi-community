package org.mi.api.user.entity;

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
 * @create: 2020-11-27 15:29
 **/
@Data
@TableName("mi_role")
@EqualsAndHashCode(callSuper = true)
public class MiRole extends BaseEntity<MiRole> {

    private static final long serialVersionUID = -6857391438758184568L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String roleName;

    private Boolean status;
}
