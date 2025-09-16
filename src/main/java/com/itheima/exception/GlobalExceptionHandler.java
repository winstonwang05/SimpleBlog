package com.itheima.exception;

import com.itheima.common.Result;
import com.itheima.common.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleSystemException(Exception e) {
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(),
                ErrorCode.SYSTEM_ERROR.getMessage());
    }
}
