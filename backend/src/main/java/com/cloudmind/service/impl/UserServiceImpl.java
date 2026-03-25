package com.cloudmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudmind.config.JwtTokenProvider;
import com.cloudmind.dto.LoginRequest;
import com.cloudmind.dto.LoginResponse;
import com.cloudmind.dto.RegisterRequest;
import com.cloudmind.entity.User;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.mapper.UserMapper;
import com.cloudmind.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void register(RegisterRequest request) {
        User exist = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());
        return new LoginResponse(token, "Bearer");
    }
}
