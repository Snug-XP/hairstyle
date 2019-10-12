package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.HairService;

import java.util.Date;

/**
 * 用于“发型师-预约列表-预约记录”页面
 */
public class OrderRecordFromOneUser {
    private Date data;
    private String hairService;
    private Double point;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getHairService() {
        return hairService;
    }

    public void setHairService(String hairService) {
        this.hairService = hairService;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }
}
