package com.itheima.pojo.DTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册信息的请求体（Json）
 */
@Data
public class UserRegisterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -303787957983040563L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
