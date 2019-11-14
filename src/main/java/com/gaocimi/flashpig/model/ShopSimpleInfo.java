package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Shop;
import lombok.Data;

@Data
public class ShopSimpleInfo {
    private int id;
    private String shopName;

    public ShopSimpleInfo(Shop shop) {
        this.id = shop.getId();
        this.shopName = shop.getShopName();
    }
}
