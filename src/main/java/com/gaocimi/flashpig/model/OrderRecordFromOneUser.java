package com.gaocimi.flashpig.model;

import java.util.Date;

/**
 * 用于“发型师-预约列表-预约记录”页面
 */
public class OrderRecordFromOneUser {
    private int orderId;
    private Date date;
    private String hairService;
    private Double point;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
