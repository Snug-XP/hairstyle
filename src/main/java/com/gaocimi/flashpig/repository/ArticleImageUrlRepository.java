package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ArticleImageUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ArticleImageUrlRepository extends JpaRepository<ArticleImageUrl, Integer>{

    ArticleImageUrl findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<ArticleImageUrl> findAll(Pageable pageable);

    /**
     * 删除该文章的所有图片url
     * @param articleId 文章id
     */
    //添加事务，不加的话会报错，目前不太了解事务的机制
    @Transactional
    public void deleteAllByArticle_Id(int articleId);
}