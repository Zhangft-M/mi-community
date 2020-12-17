package org.mi.common.core.constant;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-30 18:29
 **/
public interface SecurityConstant {

    String FROM = "from";


    String FROM_IN = "Y";

    String BEARER_TOKEN_TYPE = "Bearer";

    String VERIFY_CODE_LOGIN = "/verifyCode/login";

    String PASSWORD_LOGIN_PATH = "/oauth/login";

    String PASSWORD_CHANGE_PATH = "/user/changePassword";

    String REGISTER_PATH = "/user/register";

    String VERIFY_DATA = "verifyData";

    String[] VERIFY_CODE_VALIDATE_PATH = {VERIFY_CODE_LOGIN,REGISTER_PATH};

    String[] PASSWORD_DECODE_PATH = {PASSWORD_LOGIN_PATH,PASSWORD_CHANGE_PATH};

    String[] GOOGLE_CAPTCHA_VERIFY_PATH = {PASSWORD_LOGIN_PATH,"/post/add"};
}
