package com.gaocimi.flashpig.entity;

import javax.persistence.*;
import java.util.Date;
/**
 * Article - 文章类
 *
 * @author xp
 * @date 2019-9-23 03:00:36
 */
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String introduction;

    private Date createTime;

    private String skill;

    private String imageList;

    public Article(Integer id, String introduction, Date createTime, String skill, String imageList) {
        this.id = id;
        this.introduction = introduction;
        this.createTime = createTime;
        this.skill = skill;
        this.imageList = imageList;
    }

    public Article() {
        super();
    }

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

    public String getImageList() {
        return imageList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList == null ? null : imageList.trim();
    }
}