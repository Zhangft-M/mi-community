package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 空指针异常
 * @author: Micah
 * @create: 2020-11-07 15:42
 **/
public class NullPointException extends BaseException {
    private static final long serialVersionUID = 5161923247108315152L;

    public NullPointException() {
        super(5000, "对象为空");
    }

    public NullPointException(HttpStatus status) {
        super(status);
    }

    public NullPointException(Integer code, String message) {
        super(code, message);
    }

}
