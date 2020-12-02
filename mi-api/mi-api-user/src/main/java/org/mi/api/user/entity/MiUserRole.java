package org.mi.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-29 15:36
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MiUserRole extends BaseEntity<MiUserRole> {
    private static final long serialVersionUID = -8605809371221400315L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long userId;

    private Integer roleId;

    private Boolean hasDelete;
}
