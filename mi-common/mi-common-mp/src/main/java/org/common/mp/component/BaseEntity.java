package org.common.mp.component;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-23 15:00
 **/

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntity<T extends Model<?>> extends Model<T> {

    private static final long serialVersionUID = -5885148780827177473L;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Boolean delete;
}
