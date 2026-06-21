package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.UserLoginDTO;
import com.example.backend.dto.UserRegisterDTO;
import com.example.backend.entity.User;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    User register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @return 登录成功返回用户对象，失败返回 null
     */
    User login(UserLoginDTO dto);

    /**
     * 根据用户名查找
     */
    User findByUsername(String username);

    /**
     * 更新用户能力等级
     */
    void updateAbilityLevel(Long userId);
}
