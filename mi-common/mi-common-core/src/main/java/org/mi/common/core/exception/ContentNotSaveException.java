package org.mi.common.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @program: mi-community
 * @description: 内容不安全异常
 * @author: Micah
 * @create: 2020-11-08 18:23
 **/
public class ContentNotSaveException extends BaseException {

    private static final long serialVersionUID = 8493065344201307612L;

    public ContentNotSaveException(HttpStatus status) {
        super(status);
    }

    public ContentNotSaveException(Integer code, String message) {
        super(code, message);
    }

    public ContentNotSaveException(String message) {
        super(400, message);
    }
}
