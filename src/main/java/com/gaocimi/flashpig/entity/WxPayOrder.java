package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * WxPayOrder - 微信支付订单的记录类
 *
 * @author xp
 * @date 2020-2-22 17:01:00
 */
@Entity
@JsonIgnoreProperties(value = {"user"})
@Table(name = "wx_pay_order")
@Data
public class WxPayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    /**
//     * 该订单中按照一些参数生成的预约号
//     */
//    @Column(nullable = false)
//    private String reservationNum;

    /**
     * 提交该支付订单的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * 支付金额
     */
    private Integer money;

    /**
     * 支付订单的备注
     */
    private String note;

    /**
     * 支付订单的创建时间
     */
    private Date createTime;

    /**
     * 订单状态，“0”表示未完成，“1”表示支付已完成
     */
    @Column(nullable = false)
    private Integer status;

}