package org.mi.common.core.exception.handle;

import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.exception.BaseException;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.result.R;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @program: mi-community
 * @description: 全局异常处理
 * @author: Micah
 * @create: 2020-10-22 16:26
 **/
@Slf4j
@RestControllerAdvice(basePackages = {"org.mi.biz"})
public class GlobalExceptionHandle {

    @ExceptionHandler(BaseException.class)
    public R<String> baseExceptionHandle(BaseException e){
        log.warn("异常请求:{}",e.getMessage());
        return R.ofException(e);
    }



    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(RuntimeException.class)
    public R<String> runtimeExceptionHandle(RuntimeException e){
        log.warn("异常请求:ex=>{}",e.toString());
        return R.ofException(e.getMessage());
    }

}
