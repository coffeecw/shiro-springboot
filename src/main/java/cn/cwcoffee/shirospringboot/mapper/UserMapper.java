package cn.cwcoffee.shirospringboot.mapper;

import cn.cwcoffee.shirospringboot.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByName(String name);

    User findById(Integer id);
}
