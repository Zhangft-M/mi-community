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

    private Integer code;
    private String message;

    private HttpStatus status;

    public BaseException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
