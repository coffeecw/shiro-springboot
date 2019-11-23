package cn.cwcoffee.shirospringboot.service.impl;

import cn.cwcoffee.shirospringboot.domain.User;
import cn.cwcoffee.shirospringboot.mapper.UserMapper;
import cn.cwcoffee.shirospringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by coffeecw 2019/11/23
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByName(String name) {
        return userMapper.findByName(name);
    }
}
