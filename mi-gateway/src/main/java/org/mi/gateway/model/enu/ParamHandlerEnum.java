package org.mi.gateway.model.enu;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mi.common.core.constant.SecurityConstant;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 16:02
 **/
@Getter
public enum ParamHandlerEnum {

    /**
     * 登录处理器
     */
    PASSWORD_LOGIN_HANDLER(SecurityConstant.PASSWORD_LOGIN_PATH,"passwordLoginParamHandler"),

    REGISTER_HANDLER(SecurityConstant.REGISTER_PATH,"registerParamHandler"),

    PASSWORD_CHANGE_HANDLER(SecurityConstant.PASSWORD_CHANGE_PATH,"passwordChangeHandler"),

    VERIFY_CODE_LOGIN_HANDLER(SecurityConstant.VERIFY_CODE_LOGIN,"phoneLoginParamHandler");




    private String path;

    private String beanName;


    ParamHandlerEnum(String path, String beanName) {
        this.path = path;
        this.beanName = beanName;
    }

    public static String geBeanName(String path){
        ParamHandlerEnum[] values = ParamHandlerEnum.values();
        for (ParamHandlerEnum value : values) {
            if (value.getPath().equals(path)) {
                return value.beanName;
            }
        }
        return null;
    }

}
