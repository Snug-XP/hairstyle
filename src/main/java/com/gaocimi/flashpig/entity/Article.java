package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Article - 文章类
 *
 * @author xp
 * @date 2019-9-23 03:00:36
 */
@Entity
@Table(name = "article")
@JsonIgnoreProperties(value = {"userRecordList","handler","hibernateLazyInitializer","fieldHandler"})
@Data
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String introduction;

    private Date createTime;

    /**技巧*/
    private String skill;

    /**
     * 用户提交的对该发型文章的收藏记录列表； 定义该Article实体所有关联的UserToArticle实体； 指定mappedBy属性表明该Article实体不控制关联关系
     */
    @OneToMany(targetEntity = UserToArticle.class, mappedBy = "user")
    public List<UserToArticle> userRecordList;

    /**
     * 该文章中的图片列表； 定义该Article实体所有关联的ArticleImageUrl实体； 指定mappedBy属性表明该Article实体不控制关联关系
     */
    @OneToMany(targetEntity = ArticleImageUrl.class, mappedBy = "article")
    public List<ArticleImageUrl> articleImageUrlList;

}