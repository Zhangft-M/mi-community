package org.mi.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 异常统一接口
 * @author: Micah
 * @create: 2020-10-22 14:48
 **/

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 3700420554380777548L;

    private String message;

    private HttpStatus status;

    public BaseException(HttpStatus status) {
        this.status = status;
    }

    public BaseException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
