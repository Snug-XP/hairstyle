package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.UserToHairstylist;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-18 19:41:58
 * @description 对数据库userToHairstylist表进行相关操作
 */
public interface UserToHairstylistService {

    public List<UserToHairstylist> getUserToHairstylistList();

    public UserToHairstylist findUserToHairstylistById(int id);

    public void save(UserToHairstylist userToHairstylist);

    public void edit(UserToHairstylist userToHairstylist);

    public void delete(int id);

    public Page<UserToHairstylist> findAll(int pageNum, int pageSize);
}
