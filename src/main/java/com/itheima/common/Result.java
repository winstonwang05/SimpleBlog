package com.itheima.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 封装Result响应给前端
 * @param <T>
 */
@Setter
@Getter
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *  状态码
     */
    private Integer code;
    /**
     * 自定义返回信息
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    private Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    private Result(ResultCode resultCode, String message, T data) {
        this.code = resultCode.getCode();
        this.message = message;
        this.data = data;
    }

    // 成功（无数据）
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, null);
    }

    // 成功（有数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    // 成功（自定义消息+数据）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS, message, data);
    }

    // 失败（默认FAIL）
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAIL, null);
    }

    // 失败（自定义错误码）
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode, null);
    }

    // 失败（自定义错误码 + 数据）
    public static <T> Result<T> fail(ResultCode resultCode, T data) {
        return new Result<>(resultCode, data);
    }

    // 失败（自定义错误码 + 自定义消息）
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode, message, null);
    }

}
