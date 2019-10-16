package com.gaocimi.flashpig.model;


/**
 * 用于“发型师-数据中心-顾客列表”页面的中的数据存储
 */
public class CountUser {
    private int userId;
    private String userName;
    private String headImgUrl;
    private int count;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
