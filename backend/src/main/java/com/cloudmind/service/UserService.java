package com.cloudmind.service;

import com.cloudmind.dto.LoginRequest;
import com.cloudmind.dto.LoginResponse;
import com.cloudmind.dto.RegisterRequest;

/**
 * 用户服务接口
 */
public interface UserService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
