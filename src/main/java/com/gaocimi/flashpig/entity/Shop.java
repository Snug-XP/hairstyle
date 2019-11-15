package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * Shop - 门店类
 *
 * @author xp
 * @date 2019-11-14 17:57:34
 */
@Entity
@Table(name = "shop")
@JsonIgnoreProperties(value = {"hairstylists", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 记录本次登录的微信的openid
     */
    @Column(name = "temp_openid")
    private String openid;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 门店联系电话（也是登录账号）
     */
    private String phone;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 门店标志图片url
     */
    private String logoUrl;

    /**
     * 该门店经营执照的图片url
     */
    private String operatingLicensePictureUrl;

    /**
     * 门店所在省份
     */
    private String province;

    /**
     * 门店所在城市
     */
    private String city;

    /**
     * 门店所在区县
     */
    private String district;

    /**
     * 门店详细地址
     */
    private String address;

    /**
     * 门店经度，对于两个接近赤道的点，在纬度相等的情况下： 经度每隔0.00001度，距离相差约1米；每隔0.0001度，距离相差约10米；每隔0.001度，距离相差约100米；
     */
    private Double longitude;

    /**
     * 门店纬度，对于两个接近赤道的点，在经度相等的情况下： 纬度每隔0.00001度，距离相差约1.1米；每隔0.0001度，距离相差约11米；每隔0.001度，距离相差约111米；
     */
    private Double latitude;

    /**
     * 该门店的申请时间
     */
    private Date createTime;

    /**
     * 完成订单总数
     */
    private Integer orderSum;

    /**
     * 用户评分
     */
    private Double point;

    /**
     * 门店申请状态（0表示申请中，1表示申请通过, -1表示申请失败）
     */
    private Integer applyStatus;

    /**
     * 门店拥有的订单列表； 定义该Shop实体所有关联的Hairstylist实体； 指定mappedBy属性表明该Shop实体不控制关联关系
     */
    @OneToMany(targetEntity = Hairstylist.class, mappedBy = "shop")
    public List<Hairstylist> hairstylists;


    /**
     * 收藏该门店的用户（即粉丝）提交的对该门店的收藏记录列表； 定义该Shop实体所有关联的UserToShop实体； 指定mappedBy属性表明该Shop实体不控制关联关系
     */
//    @OneToMany(targetEntity = UserToShop.class, mappedBy = "shop")
//    public List<UserToShop> loyalUserRecordList;


    //初始化
    public Shop() {
        Date date = new Date(System.currentTimeMillis());

        setCreateTime(date);//设置注册时间
        setApplyStatus(0);//设置申请状态为申请中
        setOrderSum(0);//根据自己的订单列表（中的已完成）数量进行校正,注册时没有订单，所以为0
        setPoint(-1.0);
    }

    /**
     * 校正已完成订单数量 - 根据门店所有发型师的已完成订单数量进行校正
     */
    public void regulateOrderSum() {
        int count = 0;
        for(Hairstylist hairstylist : hairstylists){
            count += hairstylist.getOrderSum();
        }
        this.orderSum = count;
    }

    public int getOrderSum(){
        regulateOrderSum();
        return orderSum;
    }

    /**
     * 获取发型师在本月的完成订单数
     *
     * @return 本月的完成订单数
     */
    public int getCurrentMonthOrderSum() {
        int count = 0;
        for(Hairstylist hairstylist : hairstylists)
            count += hairstylist.getCurrentMonthOrderSum();
        return count;
    }

    /**
     * 获取发型师今天的预约订单数
     *
     * @return 今天的预约订单数
     */
    public int getTodayOrderSum() {
        int todayOrderCount = 0;//今日预约人数

        for(Hairstylist hairstylist : hairstylists)
            todayOrderCount += hairstylist.getTodayOrderSum();

        return todayOrderCount;
    }


}