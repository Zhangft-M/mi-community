package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 权限不足异常
 * @author: Micah
 * @create: 2020-11-07 01:01
 **/
public class NoPermissionException extends BaseException {
    private static final long serialVersionUID = 6539174033178518190L;

    public NoPermissionException() {
        super(HttpStatus.FORBIDDEN,"权限不足");
    }

    public NoPermissionException(HttpStatus status) {
        super(status);
    }

    public NoPermissionException(HttpStatus status, String message) {
        super(status, message);
    }



}
