package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.service.UserFormidService;
import com.gaocimi.flashpig.utils.MyUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

/**
 * User - 用户类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = {"haircutOrderList","articleList","hairstylistList","handler","hibernateLazyInitializer","fieldHandler"})
public class User {

    @Transient
    @Autowired
    UserFormidService userFormidService;


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

    /**用户收藏的发型师列表*/
    // 映射连接表(即中间表)为user_to_hairstylist
    @JoinTable(name = "user_to_hairstylist",
            // 定义连接表中名为user_id的外键列，该外键列参照当前实体对应表(user)的主键列(id)
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            // 定义连接表中名为hairstylist_id的外键列，该外键列参照当前实体的关联实体(Hairstylist)对应表(hairstylist)的主键列(id)
            inverseJoinColumns = @JoinColumn(name = "hairstylist_id", referencedColumnName = "id"))
    @ManyToMany(fetch = FetchType.LAZY)
    public List<Hairstylist> hairstylistList;

    /**用户提交过的Formid列表； 定义该User实体所有关联的UserFormid实体； 指定mappedBy属性表明该User实体不控制关联关系*/
    @OneToMany(targetEntity = HaircutOrder.class, mappedBy = "user",fetch = FetchType.LAZY)
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

    public List<Hairstylist> getHairstylistList() {
        return hairstylistList;
    }

    public void setHairstylistList(List<Hairstylist> hairstylistList) { this.hairstylistList = hairstylistList; }

    public List<UserFormid> getUserFormidList() { return userFormidList; }

    public void setUserFormidList(List<UserFormid> userFormidList) { this.userFormidList = userFormidList; }

    public UserFormid getOneUserFormid() {
        UserFormid userFormid;

        // 将Formid列表按提交的时间顺序排序
        Collections.sort(userFormidList, (o1, o2) -> {
            if (o1.getCreatTime().after(o2.getCreatTime())) {
                return 1;
            } else if (o2.getCreatTime().after(o1.getCreatTime())) {
                return -1;
            }
            return 0; //相等为0
        });

        //选择7天内的Formid，并删除7天以上的
        while(userFormidList.size() > 0 ){
            userFormid = userFormidList.get(0);//获取最早提交的Formid

            System.out.println(userFormidList);

            Long day = MyUtils.getDifferenceNow(userFormid.getCreatTime());//取得提交Formid的时间与当前时刻相差的天数
            if (day<7) {
                userFormidService.delete(userFormid.getId());
                return userFormid;
            }else{
                userFormidService.delete(userFormid.getId());
            }
        }
        return null;
    }


}