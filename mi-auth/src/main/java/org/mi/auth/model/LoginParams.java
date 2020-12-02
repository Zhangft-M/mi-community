package org.mi.auth.model;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

/**
 * @program: mi-community
 * @description: 登录参数
 * @author: Micah
 * @create: 2020-11-29 18:25
 **/
@Data
public class LoginParams implements Serializable {

    private static final long serialVersionUID = -7252921635206583830L;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String grantType;

    private String scopes;

    private String phoneNumber;
}
