package com.gaocimi.flashpig.service;

import java.util.List;

import com.gaocimi.flashpig.entity.User;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:19:53
 * @description 对数据库user表进行相关操作
 */
public interface UserService {

    public List<User> getUserList();

    public User findUserById(int id);

    public User findUserByOpenid(String openid);

    public void save(User user);

    public void edit(User user);

    public void delete(int id);

    public Page<User> findAll(int pageNum, int pageSize);
}
