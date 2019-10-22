package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

/**
 * User - 用户类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = { "userFormidList","haircutOrderList","articleList","hairstylistList","handler","hibernateLazyInitializer","fieldHandler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 用户姓名*/
    private String name;

    /**用户头像url*/
    private String pictureUrl;

    /** 性别（0为未知，1为男生，2为女生）*/
    private Integer sex;

    /** 对应微信用户的openid*/
    private String openid;

    /** 用户绑定的手机号码*/
    private String phoneNum;

    /**用户提交过的订单列表； 定义该User实体所有关联的HaircutOrder实体； 指定mappedBy属性表明该User实体不控制关联关系*/
    @OneToMany(targetEntity = HaircutOrder.class, mappedBy = "user",fetch = FetchType.LAZY)
    public List<HaircutOrder> haircutOrderList;

    /**用户收藏的文章列表,关系由对方维持*/
    @ManyToMany(targetEntity = Article.class,mappedBy="userList",fetch = FetchType.LAZY)
    public List<Article> articleList;

    /**
     * 该用户提交的对发型师的收藏记录列表； 定义该User实体所有关联的UserToHairstylist实体； 指定mappedBy属性表明该User实体不控制关联关系
     */
    @OneToMany(targetEntity = UserToHairstylist.class, mappedBy = "user")
    public List<UserToHairstylist> hairstylistRecordList;



    /**用户提交过的Formid列表； 定义该User实体所有关联的UserFormid实体； 指定mappedBy属性表明该User实体不控制关联关系*/
    @OneToMany(targetEntity = UserFormid.class, mappedBy = "user",fetch = FetchType.LAZY)
    public List<UserFormid> userFormidList;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<HaircutOrder> getHaircutOrderList() {
        return haircutOrderList;
    }

    public void setHaircutOrderList(List<HaircutOrder> haircutOrderList) {
        this.haircutOrderList = haircutOrderList;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public List<UserFormid> getUserFormidList() { return userFormidList; }

    public void setUserFormidList(List<UserFormid> userFormidList) { this.userFormidList = userFormidList; }

    public List<UserToHairstylist> getHairstylistRecordList() {
        return hairstylistRecordList;
    }

    public void setHairstylistRecordList(List<UserToHairstylist> hairstylistRecordList) {
        this.hairstylistRecordList = hairstylistRecordList;
    }
}