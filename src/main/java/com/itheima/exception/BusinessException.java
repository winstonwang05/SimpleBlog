package com.itheima.exception;

import com.itheima.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    /**
     *
     * @param message 自定义错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    /**
     *
     * @param message 自定义错误信息
     * @param code 自定义状态码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}
