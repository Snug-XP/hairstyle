package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.HaircutOrder;
import lombok.Data;

import java.util.Date;

/**
 * 用于“用户-我的预约”页面的数据存储（该类给用户那边用的）
 */
@Data
public class UserReservation {

    /**预约订单id*/
    private Integer orderId;

    /**预约订单的预约编号*/
    private String reservationNum;

    /**预约的发型师id*/
    private Integer hairstylistId;

    /**预约的发型师头像图片url*/
    private String imgUrl;

    /**预约的发型师姓名*/
    private String hairstylistName;

    /**预约的发型师所在门店*/
    private ShopSimpleInfo shop;

    /**预约时间*/
    private Date bookTime;

    /**预约订单状态，“-1”表示待完成，“0”表示已被通知准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消*/
    private int status;

    /**预约的服务项目名称*/
    private String serviceName;

    public UserReservation(HaircutOrder order) {
        setOrderId(order.getId());
        setReservationNum(order.getReservationNum());
        setHairstylistId(order.getHairstylist().getId());
        setImgUrl(order.getHairstylist().getPersonalPhotoUrl());
        setHairstylistName(order.getHairstylist().getHairstylistName());
        setShop(order.getHairstylist().getShopSimpleInfo());
        setBookTime(order.getBookTime());
        setStatus(order.getStatus());
        setServiceName(order.getServiceName());
    }

}
