package com.itheima.pojo.DTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录用户信息的请求体(Json)
 */
@Data
public class UserLoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1221480971639465429L;
    private String userAccount;
    private String userPassword;
}
