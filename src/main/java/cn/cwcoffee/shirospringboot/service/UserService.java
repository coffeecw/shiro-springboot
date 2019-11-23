package cn.cwcoffee.shirospringboot.service;

import cn.cwcoffee.shirospringboot.domain.User;

public interface UserService {
    User findByName(String name);
}
