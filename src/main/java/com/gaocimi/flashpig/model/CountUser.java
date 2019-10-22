package com.gaocimi.flashpig.model;


import lombok.Data;

/**
 * 用于“发型师-数据中心-顾客列表”页面的中的数据存储
 */
@Data
public class CountUser {
    private int userId;
    private String userName;
    private String headImgUrl;
    private int count;

}
