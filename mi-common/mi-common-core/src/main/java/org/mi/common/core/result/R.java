package org.mi.common.core.result;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mi.common.core.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-22 15:26
 **/
@Setter
@Getter
public class R<T> extends ResponseEntity<T> {


    /**
     * Create a new {@code ResponseEntity} with the given status code, and no body nor headers.
     *
     * @param status the status code
     */
    public R(HttpStatus status) {
        super(status);
    }

    /**
     * Create a new {@code ResponseEntity} with the given body and status code, and no headers.
     *
     * @param body   the entity body
     * @param status the status code
     */
    public R(T body, HttpStatus status) {
        super(body, status);
    }

    /**
     * Create a new {@code HttpEntity} with the given headers and status code, and no body.
     *
     * @param headers the entity headers
     * @param status  the status code
     */
    public R(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, status);
    }

    /**
     * Create a new {@code HttpEntity} with the given body, headers, and status code.
     *
     * @param body    the entity body
     * @param headers the entity headers
     * @param status  the status code
     */
    public R(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
    }

    public static <T> R<T> getInstance(T body, HttpStatus status){
        return new R<>(body,status);
    }

    /**
     * 200
     * @param <T>
     * @return
     */
    public static <T> R<T> success(){
        return new R<>(HttpStatus.OK);
    }

    /**
     * 200
     * @param <T>
     * @return
     */
    public static <T> R<T> success(T t){
        return getInstance(t,HttpStatus.OK);
    }



    /**
     * 204
     * @param <T>
     * @return
     */
    public static <T> R<T> fail(){
        return new R<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 400
     * @param <T>
     * @return
     */
    public static <T> R<T> fail(T t,HttpStatus status){
        return new R<>(t,status);
    }



    public static <T extends BaseException> R<String> ofException(T e){

        return new R<>(e.getMessage(),e.getStatus());
    }

    public static  R<String> ofException(String message){
        return new R<String>(message,HttpStatus.BAD_REQUEST);
    }
}
