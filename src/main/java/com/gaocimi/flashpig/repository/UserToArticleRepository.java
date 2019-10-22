package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserToArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserToArticleRepository extends JpaRepository<UserToArticle, Integer> , JpaSpecificationExecutor<UserToArticle> {

    UserToArticle findById(int id);

    UserToArticle findByUser_IdAndArticle_Id(int userId,int articleId);

    void deleteById(int id);

    //分页
    public Page<UserToArticle> findAll(Pageable pageable);

}