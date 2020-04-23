//package com.gaocimi.flashpig.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//import javax.persistence.*;
//import java.util.List;
//
///**
// * Tag - 发型文章的标签、关键字类
// *
// * @author xp
// * @date 2019-11-6 12:50:03
// */
//@Entity
//@Table(name = "tag")
//@JsonIgnoreProperties(value = {"articleList", "handler", "hibernateLazyInitializer", "fieldHandler"})
//public class Tag {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    /**
//     * 发型文章的标签、关键词
//     */
//    private String tagName;
//
//
//    /**
//     * 含有该标签的文章列表； 定义该Tag实体所有关联的ArticleToTag实体； 指定mappedBy属性表明该Tag实体不控制关联关系
//     */
//    @OneToMany(targetEntity = ArticleToTag.class, mappedBy = "tag")
//    public List<ArticleToTag> articleRecordList;
//
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//}