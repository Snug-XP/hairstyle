package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.UserFormid;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-17 14:54:03
 * @description 对数据库user_formid表进行相关操作
 */
public interface UserFormidService {

    public List<UserFormid> getUserFormidList();

    public UserFormid findUserFormidById(int id);

    public void save(UserFormid userFormid);

    public void edit(UserFormid userFormid);

    public void delete(int id);

    public void deleteAllByUserId(int userId);
}
