package com.voidm.springcloud.utils.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author voidm
 * ControllerAdvice注解只拦截Controller不会拦截Interceptor的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = GlobalException.class)
    public ApiResult jsonErrorHandler(HttpServletRequest request, GlobalException e) throws Exception {
        ApiResult result = new ApiResult();
        result.setCode(e.getCode());
        result.setMsg(e.getErrorMsg());
        return result;
    }
}