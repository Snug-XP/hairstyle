package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.UserToArticle;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-22 15:34:35
 * @description 对数据库user_to_article表进行相关操作
 */
public interface UserToArticleService {

    public List<UserToArticle> getUserToArticleList();

    public UserToArticle findUserToArticleById(int id);

    public void save(UserToArticle userToArticle);

    public void edit(UserToArticle userToArticle);

    public void delete(int id);

    public Page<UserToArticle> findAll(int pageNum, int pageSize);
}
