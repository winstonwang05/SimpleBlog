package com.itheima.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 * @param <T> 不同返回类型
 */
@Data
public class Result<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 数据信息
     *
     */
    private T data;
    /**
     * 信息
     */
    private String message;


    // 成功
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setData(data);
        result.setMessage("success");
        return result;
    }
    // 失败
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;

    }
}
