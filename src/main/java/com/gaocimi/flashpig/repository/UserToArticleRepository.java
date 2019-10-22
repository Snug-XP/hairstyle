package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserToArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToArticleRepository extends JpaRepository<UserToArticle, Integer>{

    UserToArticle findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<UserToArticle> findAll(Pageable pageable);
}