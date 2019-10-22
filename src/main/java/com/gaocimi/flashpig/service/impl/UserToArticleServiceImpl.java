package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.UserToArticle;
import com.gaocimi.flashpig.repository.UserToArticleRepository;
import com.gaocimi.flashpig.service.UserToArticleService;
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
 * @date 2019-10-22 15:35:21
 * @description 对数据库user_to_article表进行相关操作的实现类
 */
@Service
public class UserToArticleServiceImpl implements UserToArticleService {
    protected static final Logger logger = LoggerFactory.getLogger(UserToArticleServiceImpl.class);

    @Autowired
    public UserToArticleRepository userToArticleRepository;

    @Override
    public List<UserToArticle> getUserToArticleList() {
        return userToArticleRepository.findAll();
    }

    @Override
    public UserToArticle findUserToArticleById(int id) {
        return userToArticleRepository.findById(id);
    }

    @Override
    public void save(UserToArticle userToArticle) {
        userToArticleRepository.save(userToArticle);
    }

    @Override
    public void edit(UserToArticle userToArticle) {
        userToArticleRepository.save(userToArticle);
    }

    @Override
    public void delete(int id) {
        userToArticleRepository.deleteById(id);
    }


    public List<UserToArticle> findAll() {
        return userToArticleRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<UserToArticle> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<UserToArticle> userToArticlePage = null;
        try {
            userToArticlePage = userToArticleRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return userToArticlePage;
    }
}


