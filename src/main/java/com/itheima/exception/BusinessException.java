package com.itheima.exception;

import com.itheima.common.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -692615954102045667L;
    // 自定义错误信息
    private final String message;
    // 自定义错误码
    private final int code;

    /**
     *
     * @param errorCode 枚举类错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    /**
     *
     * @param code 自定义错误码
     * @param message 自定义错误信息`
     */
    public BusinessException(int code,String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

}
