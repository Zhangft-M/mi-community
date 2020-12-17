package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 未认证错误
 * @author: Micah
 * @create: 2020-11-07 00:54
 **/
public class UnauthorizedException extends BaseException {
    private static final long serialVersionUID = -2184434921470317696L;

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED,"未认证");
    }

    public UnauthorizedException(HttpStatus status) {
        super(status);
    }

    public UnauthorizedException(HttpStatus status, String message) {
        super(status, message);
    }

}
