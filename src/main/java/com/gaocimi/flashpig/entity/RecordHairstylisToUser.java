package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * RecordHairstylisToUser - ‘发型师对顾客的备注’类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "record_hairstylis_to_user")
@JsonIgnoreProperties(value = {"hairstylist"})//,"handler","hibernateLazyInitializer"})
public class RecordHairstylisToUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**创建时间*/
    private Date createTime;

    /**备注内容*/
    private String content;

    /**图片列表*/
    private String imageList;

    /**提交该备注的发型师； 定义名为createId的外键列，该外键引用hairstylist表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "create_id", nullable = false)
    public Hairstylist hairstylist;

    /**该备注的对象用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getImageList() {
        return imageList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList == null ? null : imageList.trim();
    }

    public Hairstylist getHairstylist() {
        return hairstylist;
    }

    public void setHairstylist(Hairstylist hairstylist) {
        this.hairstylist = hairstylist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}