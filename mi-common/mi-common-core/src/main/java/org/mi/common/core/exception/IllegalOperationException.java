package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 非法操作异常
 * @author: Micah
 * @create: 2020-12-19 21:44
 **/
public class IllegalOperationException extends BaseException {
    public IllegalOperationException(HttpStatus status) {
        super(status);
    }

    public IllegalOperationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
