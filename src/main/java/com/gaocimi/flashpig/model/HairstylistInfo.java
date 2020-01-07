package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.UserToHairstylist;
import lombok.Data;

import java.util.Date;

/**
 * 用于“用户-我的发型师”页面的数据存储
 */
@Data
public class HairstylistInfo {
    private int hairstylistId;
    private String name;
    private ShopSimpleInfo shop;
    private String personalPhotoUrl;
    private Double point;
    private Integer orderSum;
    private Integer businessStatus;
    private Date recordCreatTime;

    public HairstylistInfo(UserToHairstylist collectionRecord) {
        this.hairstylistId = collectionRecord.hairstylist.getId();
        this.name = collectionRecord.hairstylist.getHairstylistName();
        this.shop = collectionRecord.hairstylist.getShopSimpleInfo();
        this.personalPhotoUrl = collectionRecord.hairstylist.getPersonalPhotoUrl();
        this.point = collectionRecord.hairstylist.getPoint();
        this.orderSum = collectionRecord.hairstylist.getOrderSum();
        this.businessStatus = collectionRecord.hairstylist.getBusinessStatus();
        this.recordCreatTime = collectionRecord.getCreateTime();
    }

    public HairstylistInfo(Hairstylist hairstylist) {
        this.hairstylistId = hairstylist.getId();
        this.name = hairstylist.getHairstylistName();
        this.shop = hairstylist.getShopSimpleInfo();
        this.personalPhotoUrl = hairstylist.getPersonalPhotoUrl();
        this.point = hairstylist.getPoint();
        this.orderSum = hairstylist.getOrderSum();
        this.businessStatus = hairstylist.getBusinessStatus();
        this.recordCreatTime = null;
    }

    public HairstylistInfo() {
        super();
    }
}
