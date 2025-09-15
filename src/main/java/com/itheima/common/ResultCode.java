package com.itheima.common;

import lombok.Getter;

/**
 * 枚举类（状态码及信息）
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "成功"),
    FAIL(400, "失败"), // 客户端错误
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常"),

    // 常见业务错误
    USER_EXIST(1001, "用户已存在"),
    PARAM_ERROR(1002, "参数错误"),
    DATA_NOT_FOUND(1003, "数据不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}