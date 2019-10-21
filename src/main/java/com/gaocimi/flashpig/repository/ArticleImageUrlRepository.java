package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ArticleImageUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleImageUrlRepository extends JpaRepository<ArticleImageUrl, Integer>{

    ArticleImageUrl findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<ArticleImageUrl> findAll(Pageable pageable);
}