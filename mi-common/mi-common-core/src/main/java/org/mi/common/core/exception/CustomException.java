package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 自定义异常
 * @author: Micah
 * @create: 2020-11-07 01:03
 **/
public class CustomException extends BaseException {
    private static final long serialVersionUID = 4955908952622817615L;

    public CustomException(HttpStatus status) {
        super(status);
    }

    public CustomException(Integer code, String message) {
        super(code, message);
    }

}
