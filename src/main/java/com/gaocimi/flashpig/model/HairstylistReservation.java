package com.gaocimi.flashpig.model;

import java.util.Date;

/**
 * 用于“发型师-预约列表”页面的信息
 */
public class HairstylistReservation {

    /**预约订单id*/
    private Integer orderId;

    /**提交预约的用户id*/
    private Integer userId;

    /**提交预约的用户头像图片url*/
    private String imgUrl;

    /**提交预约的用户姓名*/
    private String userName;

    /**预约订单中留的电话号码*/
    private String userPhone;

    /**创建的时间*/
    private Date creatTime;

    /**预约时间*/
    private Date bookTime;

    /**预约订单状态 “0”表示订单正在进行中，”1“表示已完成，”-1“表示订单已取消，”-2“表示订单被拒绝*/
    private int status;


    /**预约项目*/
    private String hairService;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getBookTime() {
        return bookTime;
    }

    public void setBookTime(Date bookTime) {
        this.bookTime = bookTime;
    }

    public String getHairService() {
        return hairService;
    }

    public void setHairService(String hairService) {
        this.hairService = hairService;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
