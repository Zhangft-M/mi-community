package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-31 15:03
 **/
public class FeignException extends BaseException{

    private static final long serialVersionUID = -1226392923328356797L;

    public FeignException(HttpStatus status, String message) {
        super(status, message);
    }


}
