//package com.gaocimi.flashpig.repository;
//
//import com.gaocimi.flashpig.entity.ArticleToTag;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//
//public interface ArticleToTagRepository extends JpaRepository<ArticleToTag, Integer> {
//
//    ArticleToTag findById(int id);
//
//    boolean existsByArticleIdAndTagId(int articleId, int tagId);
//
//    //看一下是否2个方法都有用？
//    boolean existsByArticle_IdAndTag_Id(int articleId, int tagId);
//
//    void deleteById(int id);
//
//    //分页
//    public Page<ArticleToTag> findAll(Pageable pageable);
//}