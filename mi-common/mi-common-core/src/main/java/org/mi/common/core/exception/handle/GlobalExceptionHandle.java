package org.mi.common.core.exception.handle;

import org.mi.common.core.exception.BaseException;
import org.mi.common.core.result.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @program: mi-community
 * @description: 全局异常处理
 * @author: Micah
 * @create: 2020-10-22 16:26
 **/
@RestControllerAdvice(basePackages = "org.mi")
public class GlobalExceptionHandle {

    @ExceptionHandler(BaseException.class)
    public R<BaseException> baseExceptionHandle(BaseException e){
        return R.ofException(e);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R<String> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        String message = "";
        if (bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            if (null != fieldError){
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return R.fail(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BindException.class)
    public R<String> bindExceptionHandle(BindException bindException){
        BindingResult bindingResult = bindException.getBindingResult();
        String message = "";
        if (bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            if (null != fieldError){
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return R.fail(message, HttpStatus.BAD_REQUEST);
    }

}
