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

    /**
     * 提交该支付订单的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * 支付类型（0表示购买会员【90天】的订单，1表示购买会员【180天】的订单，2表示购买会员【365天】的订单）
     */
    private Integer type;

    /**
     * 支付金额
     */
    private Integer money;

    /**
     * 支付订单的商品名称（类似备注）
     */
    private String body;

    /**
     * 支付订单的创建时间
     */
    private Date createTime;

    /**
     * 支付订单的结束时间
     */
    private Date endTime;

    /**
     * 订单状态，“0”表示未完成，“1”表示支付已完成
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 调用统一下单接口后生成的预订单id
     */
    private String prepayId;
}