package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * UserFormid - 用户Formid实体类
 *
 * @author xp
 * @date 2019-10-17 14:41:35
 */
@Entity
@Table(name = "user_formid")
@JsonIgnoreProperties(value = {"user","hairstylistList","handler","hibernateLazyInitializer","fieldHandler"})
public class UserFormid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**提交该Formid的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**用户openid*/
    private String openid;

    /** 该用户提交的一个Formid*/
    private String formid;

    /** 用户提交该Formid的时间*/
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}