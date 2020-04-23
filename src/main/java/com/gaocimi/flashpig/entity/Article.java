package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.model.HairstylistInfo;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Article - 发型文章类
 *
 * @author xp
 * @date 2019-9-23 03:00:36
 */
@Entity
@Table(name = "article")
@JsonIgnoreProperties(value = {"hairstylist", "userRecordList", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 标题
     */
    private String title;

    /**
     * 发型文章内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 发型文章的状态（“0”表示审核中，“1”表示审核通过，“-1”表示审核失败）
     */
    private Integer status;

    /**
     * 标签、关键词
     */
    private String tag;


    /**
     * 发表该文章的发型师； 定义名为hairstylist_id的外键列，该外键引用hairstylist表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstylist_id", nullable = false)
    public Hairstylist hairstylist;


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


    public String[] getTag() {
        if (tag == null)
            return null;
        return tag.split(",");
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTag(List<String> tagList) {
        this.tag = "";
        for (String tag : tagList) {
            this.tag += "," + tag.trim();
        }
        if (this.tag.length() > 0)
            this.tag = this.tag.substring(1);
    }

    public void addTag(List<String> tagList) {
        for (String tag : tagList) {
            if (this.tag == null || this.tag.length() == 0)
                this.tag = tag.trim();
            else
                this.tag += "," + tag.trim();
        }
    }

//    public HairstylistInfo getHairstylist() {
//        if (this.hairstylist == null)
//            return null;
//        else
//            return new HairstylistInfo(this.hairstylist);
//    }


}