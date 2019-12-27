package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Shop;
import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class ShopSimpleInfo {
    private int id;
    private String shopName;
    private String address;
    private Integer applyStatus;

    public ShopSimpleInfo(Shop shop) {
        if(shop!=null) {
            this.id = shop.getId();
            this.shopName = shop.getShopName();
            this.address = shop.getProvince() + " " + shop.getCity() + " " + shop.getDistrict() + " " + shop.getAddress();
            this.applyStatus = shop.getApplyStatus();
        }
    }
}
