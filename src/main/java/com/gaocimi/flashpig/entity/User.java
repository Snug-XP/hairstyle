package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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
@JsonIgnoreProperties(value = { "userFormidList","haircutOrderList","articleRecordList","haircutOrderList","handler","hibernateLazyInitializer","fieldHandler"})
@Data
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


    /**
     * 该用户提交的对发型文章的收藏记录列表； 定义该User实体所有关联的UserToArticle实体； 指定mappedBy属性表明该User实体不控制关联关系
     */
    @OneToMany(targetEntity = UserToArticle.class, mappedBy = "user")
    public List<UserToArticle> articleRecordList;

    /**
     * 该用户提交的对发型师的收藏记录列表； 定义该User实体所有关联的UserToHairstylist实体； 指定mappedBy属性表明该User实体不控制关联关系
     */
    @OneToMany(targetEntity = UserToHairstylist.class, mappedBy = "user")
    public List<UserToHairstylist> hairstylistRecordList;


    /**用户提交过的Formid列表； 定义该User实体所有关联的UserFormid实体； 指定mappedBy属性表明该User实体不控制关联关系*/
    @OneToMany(targetEntity = UserFormid.class, mappedBy = "user",fetch = FetchType.LAZY)
    public List<UserFormid> userFormidList;

}