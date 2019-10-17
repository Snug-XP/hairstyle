package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.UserFormid;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.repository.UserFormidRepository;
import com.gaocimi.flashpig.service.UserFormidService;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-17 14:55:21
 * @description 对数据库user_formid表进行相关操作的实现类
 */
@Service
public class UserFormidServiceImpl implements UserFormidService {
    protected static final Logger logger = LoggerFactory.getLogger(UserFormidServiceImpl.class);

    @Autowired
    public UserFormidRepository userFormidRepository;
    @Autowired
    public UserService userService;

    @Override
    public List<UserFormid> getUserFormidList() {
        return userFormidRepository.findAll();
    }

    @Override
    public UserFormid findUserFormidById(int id) {
        return userFormidRepository.findById(id);
    }

    @Override
    public void save(UserFormid userFormid) {
        userFormidRepository.save(userFormid);
    }

    @Override
    public void edit(UserFormid userFormid) {
        userFormidRepository.save(userFormid);
    }

    @Override
    public void delete(int id) {
        userFormidRepository.deleteById(id);
    }


    public List<UserFormid> findAll()
    {
        return userFormidRepository.findAll();
    }

    public void deleteAllByUserId(int userId){
        User user = userService.findUserById(userId);
        for(UserFormid hs : user.getUserFormidList()){
            userFormidRepository.deleteById(hs.getId());
        }
    }


}


