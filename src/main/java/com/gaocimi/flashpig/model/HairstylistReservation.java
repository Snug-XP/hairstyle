package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.HaircutOrder;

import java.util.Date;

/**
 * 用于“发型师-预约列表”页面的数据存储（该类给发型师那边用的）
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
    private Date createTime;

    /**预约时间*/
    private Date bookTime;

    /**预约订单状态，“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消*/
    private int status;

    /**预约的服务项目名称*/
    private String serviceName;

    /**该订单选取的服务项目描述*/
    public String description;

    /**该订单选取的服务项目大致价格*/
    public Double price;


    public HairstylistReservation(HaircutOrder order) {
        setOrderId(order.getId());
        setUserPhone(order.getUserPhone());
        setUserId(order.user.getId());
        setImgUrl(order.user.getPictureUrl());
        setUserName(order.getUserName());
        setCreateTime(order.getCreateTime());
        setBookTime(order.getBookTime());
        setServiceName(order.getServiceName());
        setDescription(order.getDescription());
        setPrice(order.getPrice());
        setStatus(order.getStatus());
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getBookTime() {
        return bookTime;
    }

    public void setBookTime(Date bookTime) {
        this.bookTime = bookTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
