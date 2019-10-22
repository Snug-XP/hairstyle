package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * UserToArticle - 用户收藏文章的中间表记录类
 *
 * @author xp
 * @date 2019-10-22 15:32:44
 */
@Entity
@Table(name = "user_to_article")
@JsonIgnoreProperties(value = {"user","article","handler","hibernateLazyInitializer","fieldHandler"})
public class UserToArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date createTime;

    /**提交该收藏记录的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**该收藏记录对应的文章； 定义名为article_id的外键列，该外键引用article表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = Article.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    public Article article;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}