package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer>, JpaSpecificationExecutor<Article> {

    Article findById(int id);
    
    void deleteById(int id);

    List<Article> findAllByTagLike(String tag);

    List<Article> findAllByTagLikeAndStatus(String tag,Integer status);

    List<Article> findAllByTitleLike(String title);

    List<Article> findAllByContentLike(String Content);


    //分页
    public Page<Article> findAll(Pageable pageable);

    List<Article> findAllByStatus(int status);
    List<Article> findAllByStatusIsNot(int status);

    long countAllByStatus(int status);
}