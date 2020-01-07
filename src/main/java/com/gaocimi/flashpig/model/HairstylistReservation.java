package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.HaircutOrder;
import lombok.Data;

import java.util.Date;

/**
 * 用于“发型师-预约列表”页面的数据存储（该类给发型师那边用的）
 */
@Data
public class HairstylistReservation {

    /**
     * 序号
     */
    private Integer index;

    /**
     * 预约订单id
     */
    private Integer orderId;

    /**
     * 预约订单的预约编号
     */
    private String reservationNum;

    /**
     * 提交预约的用户id
     */
    private Integer userId;

    /**
     * 提交预约的用户头像图片url
     */
    private String userHeadImgUrl;

    /**
     * 提交预约的用户姓名
     */
    private String userName;

    /**
     * 提交预约的用户姓氏
     */
    private String userLastName;

    /**
     * 预约订单中留的电话号码
     */
    private String userPhone;

    /**
     * 创建的时间
     */
    private Date createTime;

    /**
     * 预约时间
     */
    private Date bookTime;

    /**
     * 预约订单状态，“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消
     */
    private int status;

    /**
     * 预约的服务项目名称
     */
    private String serviceName;

    /**
     * 该订单选取的服务项目描述
     */
    public String description;

    /**
     * 该订单选取的服务项目大致价格
     */
    public Double price;


    public HairstylistReservation(int index, HaircutOrder order) {
        setIndex(index);
        setOrderId(order.getId());
        setReservationNum(order.getReservationNum());
        setUserPhone(order.getUserPhone());
        setUserId(order.user.getId());
        setUserHeadImgUrl(order.user.getPictureUrl());
        setUserName(order.user.getName());
        setUserName(order.user.getName());
        setUserLastName(order.user.getLastName());
        setCreateTime(order.getCreateTime());
        setBookTime(order.getBookTime());
        setServiceName(order.getServiceName());
        setDescription(order.getDescription());
        setPrice(order.getPrice());
        setStatus(order.getStatus());
    }

    public HairstylistReservation(HaircutOrder order) {
        setOrderId(order.getId());
        setReservationNum(order.getReservationNum());
        setUserPhone(order.getUserPhone());
        setUserId(order.user.getId());
        setUserHeadImgUrl(order.user.getPictureUrl());
        setUserName(order.user.getName());
        setUserLastName(order.user.getLastName());
        setCreateTime(order.getCreateTime());
        setBookTime(order.getBookTime());
        setServiceName(order.getServiceName());
        setDescription(order.getDescription());
        setPrice(order.getPrice());
        setStatus(order.getStatus());
    }
    public HairstylistReservation(){
        super();
    }
}
