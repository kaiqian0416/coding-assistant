package com.example.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dto.UserLoginDTO;
import com.example.backend.dto.UserRegisterDTO;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public User register(UserRegisterDTO dto) {
        if (userMapper.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        // 邮箱为空字符串时转为 null，避免数据库唯一索引冲突
        String email = dto.getEmail();
        if (email != null && !email.isBlank()) {
            if (userMapper.findByEmail(email).isPresent()) {
                throw new RuntimeException("邮箱已被注册");
            }
        } else {
            email = null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(email);
        user.setRole("user");
        user.setAbilityLevel("easy");
        save(user);
        return user;
    }

    @Override
    public User login(UserLoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username).orElse(null);
    }

    @Override
    public void updateAbilityLevel(Long userId) {
        // 预留
    }
}
