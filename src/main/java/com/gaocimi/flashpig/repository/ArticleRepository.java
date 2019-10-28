package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer>{

    Article findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<Article> findAll(Pageable pageable);

    List<Article> findAllByStatus(int status);
}