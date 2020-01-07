package com.gaocimi.flashpig.model;


import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.User;
import lombok.Data;

/**
 * 用于“发型师-数据中心-顾客列表”页面的中的数据存储
 */
@Data
public class CountUser {
    private int userId;
    private String userName;
    private String userLastName;
    private String headImgUrl;
    private int count;

    public CountUser(HaircutOrder order) {
        setUserId(order.user.getId());
        setUserName(order.user.getName());
        setUserLastName(order.user.getLastName());
        setHeadImgUrl(order.user.getPictureUrl());
        setCount(0);
    }
    public CountUser(User user) {
        setUserId(user.getId());
        setUserName(user.getName());
        setUserLastName(user.getLastName());
        setHeadImgUrl(user.getPictureUrl());
        setCount(0);
    }
}
