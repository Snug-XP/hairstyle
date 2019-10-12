package com.gaocimi.flashpig.model;

import java.util.Date;

/**
 * 用于“发型师-预约列表”页面的信息
 */
public class HairstylistReservation {

    /**预约单id*/
    private Integer reservationId;

    /**提交预约的用户id*/
    private Integer userId;

    /**提交预约的用户头像图片url*/
    private String imgUrl;

    /**提交预约的用户姓名*/
    private String userName;

    /**预约单中留的电话号码*/
    private String userPhone;

    /**预约时间（不是创建的时间）*/
    private Date time;

    /**预约单中状态*/
    private int status;


    /**预约项目*/
    private String hairService;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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
