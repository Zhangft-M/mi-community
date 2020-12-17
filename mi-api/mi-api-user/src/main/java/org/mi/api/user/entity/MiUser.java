package org.mi.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.common.mp.component.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 18:34
 **/
@Data
@TableName("mi_user")
@EqualsAndHashCode(callSuper = true)
public class MiUser extends BaseEntity<MiUser> {

    private static final long serialVersionUID = 5926342485081151762L;


    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    private String password;

    private String phone;

    @Email
    private String email;

    private String nickName;

    private String avatar;

    private Boolean status;

    private String sign;

    private Integer point;

    private Integer gender;

    private Integer postCount;

    private String lastLoginIp;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @TableField(exist = false)
    private Set<MiRole> roles;


}
