package org.mi.common.core.config;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.exception.FeignException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class ExceptionErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        try {
            if (response.body() != null) {
                String result = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                log.warn("接口调用抛出异常:ex=>{}", result);
                return new FeignException(HttpStatus.BAD_REQUEST, result);
            }
        } catch (Exception e) {
            return e;
        }
        return null;
    }

}