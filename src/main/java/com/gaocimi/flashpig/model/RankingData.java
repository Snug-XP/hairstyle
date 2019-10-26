package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Hairstylist;
import lombok.Data;

@Data
public class RankingData {
    private int index;
    private int hairstylistId;
    private String personalPhotoUrl;
    private String hairstylistName;
    private int oederSum;

    public RankingData(int index, Hairstylist hairstylist, int oederSum) {
        this.index = index;
        this.hairstylistId = hairstylist.getId();
        this.personalPhotoUrl = hairstylist.getPersonalPhotoUrl();
        this.hairstylistName = hairstylist.getHairstylistName();
        this.oederSum = oederSum;
    }

    public RankingData() {
        super();
    }
}
