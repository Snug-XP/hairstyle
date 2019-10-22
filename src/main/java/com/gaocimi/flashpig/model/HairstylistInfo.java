package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.UserToHairstylist;
import lombok.Data;

import java.util.Date;

/**用于“用户-我的发型师”页面的数据存储*/
@Data
public class HairstylistInfo {
    private int hairstylistId;
    private String name;
    private String shopNmae;
    private String personalPhotoUrl;
    private String addres;
    private Double point;
    private Date recordCreatTime;

    public HairstylistInfo(UserToHairstylist collectionRecord) {
        this.hairstylistId = collectionRecord.hairstylist.getId();
        this.name = collectionRecord.hairstylist.getHairstylistName();
        this.shopNmae = collectionRecord.hairstylist.getShopName();
        this.personalPhotoUrl = collectionRecord.hairstylist.getPersonalPhotoUrl();
        this.addres = collectionRecord.hairstylist.getAddress();
        this.point = collectionRecord.hairstylist.getPoint();
        this.recordCreatTime =collectionRecord.getCreateTime();
    }
    public HairstylistInfo(){
        super();
    }
}
