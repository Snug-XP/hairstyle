package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.Shop;
import lombok.Data;

@Data
public class RankingData {
    private int index;
    private int id;
    private String photoUrl;
    private String name;
    private int oederSum;

    public RankingData(int index, Hairstylist hairstylist, int oederSum) {
        this.index = index;
        this.id = hairstylist.getId();
        this.photoUrl = hairstylist.getPersonalPhotoUrl();
        this.name = hairstylist.getHairstylistName();
        this.oederSum = oederSum;
    }

    public RankingData(int index, Shop shop, int oederSum) {
        this.index = index;
        this.id = shop.getId();
        this.photoUrl = shop.getShopPhotoUrl();
        this.name = shop.getShopName();
        this.oederSum = oederSum;
    }

    public RankingData() {
        super();
    }
}
