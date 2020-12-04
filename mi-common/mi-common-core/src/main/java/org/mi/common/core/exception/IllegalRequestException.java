package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-04 14:12
 **/
public class IllegalRequestException extends BaseException{
    public IllegalRequestException(HttpStatus status) {
        super(status);
    }
    public IllegalRequestException(String message) {
        super(400, message);
    }


    public IllegalRequestException(Integer code, String message) {
        super(code, message);
    }
}
