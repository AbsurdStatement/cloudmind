package com.cloudmind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应参数
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
}
