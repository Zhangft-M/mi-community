package org.mi.common.core.exception.handle;

import org.mi.common.core.exception.BaseException;
import org.mi.common.core.result.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @program: mi-community
 * @description: 全局异常处理
 * @author: Micah
 * @create: 2020-10-22 16:26
 **/
@RestControllerAdvice
public class GlobalExceptionHandle {


    @ExceptionHandler(BaseException.class)
    public R<BaseException> baseExceptionHandle(BaseException e){
        return R.ofException(e);
    }

}
