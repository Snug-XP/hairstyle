package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(value = {"userList"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String introduction;

    private Date createTime;

    /**技巧*/
    private String skill;

    /**收藏该文章的用户列表*/
    @JoinTable(name="user_to_article",
            joinColumns={@JoinColumn(name="article_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
    @ManyToMany(targetEntity = User.class,fetch = FetchType.LAZY)
    public List<User> userList;

    /**
     * 该文章中的图片列表； 定义该Article实体所有关联的ArticleImageUrl实体； 指定mappedBy属性表明该Article实体不控制关联关系
     */
    @OneToMany(targetEntity = ArticleImageUrl.class, mappedBy = "article")
    public List<ArticleImageUrl> articleImageUrlList;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setSkill(String skill) {
        this.skill = skill == null ? null : skill.trim();
    }

    public String getSkill() {
        return skill;
    }

//不能取消注释，否则序列化时和User造成死循环使得栈溢出(可利用公有方式访问该属性)
//    public List<User> getUserList() {
//        return userList;
//    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<ArticleImageUrl> getArticleImageUrlList() {
        return articleImageUrlList;
    }

    public void setArticleImageUrlList(List<ArticleImageUrl> articleImageUrlList) {
        this.articleImageUrlList = articleImageUrlList;
    }
}