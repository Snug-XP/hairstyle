package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * User - 用户类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = {"sessionKey", "openid","userFormidList","haircutOrderList","hairstylistRecordList","articleRecordList","haircutOrderList","handler","hibernateLazyInitializer","fieldHandler"})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 用户姓名*/
    private String name;

    /** 用户姓名*/
    private String lastName;

    /**用户头像url*/
    private String pictureUrl;

    /** 性别（0为未知，1为男生，2为女生）*/
    private Integer sex;

    /** 对应微信用户的openid*/
    private String openid;

    /** 用户绑定的手机号码*/
    private String phoneNum;

    /** 用户微信临时的sessionKey*/
    private String sessionKey;

    /**
     * 用户最后位置的经度，对于两个接近赤道的点，在纬度相等的情况下： 经度每隔0.00001度，距离相差约1米；每隔0.0001度，距离相差约10米；每隔0.001度，距离相差约100米；
     */
    private Double longitude;

    /**
     * 用户最后位置的纬度，对于两个接近赤道的点，在经度相等的情况下： 纬度每隔0.00001度，距离相差约1.1米；每隔0.0001度，距离相差约11米；每隔0.001度，距离相差约111米；
     */
    private Double latitude;

    /**
     * 用户是否是vip(会员)用户（1表示该用户是vip,0或其它数字表示该用户不是vip）
     */
    private Integer isVip;

    /**
     * 用户的会员到期时间，如果为空表示未开通过会员
     */
    private Date vipEndTime;

    /**用户提交过的订单列表； 定义该User实体所有关联的HaircutOrder实体； 指定mappedBy属性表明该User实体不控制关联关系*/
    @OneToMany(targetEntity = HaircutOrder.class, mappedBy = "user")
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
    @OneToMany(targetEntity = UserFormid.class, mappedBy = "user")
    public List<UserFormid> userFormidList;

/**************************************************************************************************************************/
    /**
     * 判断该用户是否收藏了发型师
     *
     * @param hairstylistId 发型师的id
     * @return 用户是否收藏了发型师
     */
    public boolean isLoyalToHairstylist(int hairstylistId) {
        List<UserToHairstylist> recordList = getHairstylistRecordList();
        if (recordList == null || recordList.size() == 0) {
            return false;
        }
        for (UserToHairstylist h : recordList) {
            if (h.getHairstylist().getId() == hairstylistId )
                return true;
        }
        return false;
    }

    public boolean isVIP(){
        if(isVip==1) return true;
        else return false;
    }
}