package org.mi.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: mi-community
 * @description: 自定义权限不足异常
 * @author: Micah
 * @create: 2020-10-25 01:13
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler extends OAuth2AccessDeniedHandler {

    private final ObjectMapper objectMapper;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException authException) throws IOException, ServletException {
        log.info("授权失败，禁止访问 {}", request.getRequestURI());
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/json;charset=utf-8");
        Map<String,String> result = new HashMap<>(2);
        result.put("msg","权限不足，拒绝访问");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(result));
    }
}
