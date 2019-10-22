package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.UserToHairstylist;
import com.gaocimi.flashpig.repository.UserToHairstylistRepository;
import com.gaocimi.flashpig.service.UserToHairstylistService;
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
 * @date 2019-10-18 19:43:22
 * @description 对数据库user_to_hairstylist表进行相关操作的实现类
 */
@Service
public class UserToHairstylistServiceImpl implements UserToHairstylistService {
    protected static final Logger logger = LoggerFactory.getLogger(UserToHairstylistServiceImpl.class);

    @Autowired
    public UserToHairstylistRepository userToHairstylistRepository;

    @Override
    public List<UserToHairstylist> getUserToHairstylistList() {
        return userToHairstylistRepository.findAll();
    }

    @Override
    public UserToHairstylist findUserToHairstylistById(int id) {
        return userToHairstylistRepository.findById(id);
    }

    @Override
    public UserToHairstylist findByUserAndHairstylist(int userId, int hairstylistId){
        return userToHairstylistRepository.findByUser_IdAndHairstylist_Id(userId,hairstylistId);
    }

    @Override
    public void save(UserToHairstylist userToHairstylist) {
        userToHairstylistRepository.save(userToHairstylist);
    }

    @Override
    public void edit(UserToHairstylist userToHairstylist) {
        userToHairstylistRepository.save(userToHairstylist);
    }

    @Override
    public void delete(int id) {
        userToHairstylistRepository.deleteById(id);
    }


    public List<UserToHairstylist> findAll() {
        return userToHairstylistRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<UserToHairstylist> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<UserToHairstylist> userToHairstylistPage = null;
        try {
            userToHairstylistPage = userToHairstylistRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return userToHairstylistPage;
    }
}


