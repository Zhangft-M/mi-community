package org.mi.api.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import java.io.Serializable;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 18:51
 **/
@Data
public class MiUserDTO implements Serializable {

    private static final long serialVersionUID = -3271309575266715994L;

    private Long id;

    private String username;

    private String phone;

    @Email
    private String email;

    private String nickName;

    private String backgroundImage;

    private String avatar;

    private String sign;

    private Integer point;

    private Integer gender;

    private Integer postCount;

    public String getGenderName() {
        return gender == 1 ? "男":"女";
    }
}
