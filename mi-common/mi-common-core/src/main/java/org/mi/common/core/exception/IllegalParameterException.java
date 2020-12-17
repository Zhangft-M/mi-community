package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 非法参数异常
 * @author: Micah
 * @create: 2020-11-07 00:28
 **/
public class IllegalParameterException extends BaseException {

    private static final long serialVersionUID = 3489600101061221271L;

    public IllegalParameterException() {
        super(HttpStatus.BAD_REQUEST,"参数错误");
    }

    public IllegalParameterException(String message) {
        super(HttpStatus.BAD_REQUEST,message);
    }

    public IllegalParameterException(HttpStatus status) {
        super(status);
    }

    public IllegalParameterException(HttpStatus status, String message) {
        super(status, message);
    }
}
