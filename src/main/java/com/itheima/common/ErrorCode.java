package com.itheima.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "success"),
    PARAMS_ERROR(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或无权限"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "请求数据不存在"),
    SYSTEM_ERROR(500, "系统内部异常");
    private final int code;
    private final String message;

    /**
     * 错误码枚举
     * @param code 自定义错误码
     * @param message 自定义错误信息
     */
    ErrorCode (int code,String message) {
        this.code = code;
        this.message = message;
    }
}
