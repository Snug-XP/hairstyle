//package com.gaocimi.flashpig.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.Data;
//
//import javax.persistence.*;
//import java.util.Date;
//
///**
// * ArticleToTag - 发型文章对应标签关键字的中间表记录类
// *
// * @author xp
// * @date 2019-11-6 13:15:22
// */
//@Entity
//@Table(name = "article_to_tag")
//@JsonIgnoreProperties(value = {"tag", "article", "handler", "hibernateLazyInitializer", "fieldHandler"})
//@Data
//public class ArticleToTag {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    /**
//     * 该记录对应的发型文章； 定义名为article_id的外键列，该外键引用article表的主键(id)列,采用懒加载
//     */
//    @ManyToOne(targetEntity = Article.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "article_id", nullable = false)
//    public Article article;
//
//    /**
//     * 该记录对应的标签、关键字； 定义名为tag_id的外键列，该外键引用tag表的主键(id)列,采用懒加载
//     */
//    @ManyToOne(targetEntity = Tag.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "tag_id", nullable = false)
//    public Tag tag;
//
//    public ArticleToTag(Article article, Tag tag) {
//        this.article = article;
//        this.tag = tag;
//    }
//
//    public ArticleToTag() {
//        super();
//    }
//}