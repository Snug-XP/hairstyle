package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.model.ProductInfo;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


/**
 * ProductOrder - 产品订单类
 *
 * @author xp
 * @date 2020-5-2 10:54:07
 */
@Entity
@JsonIgnoreProperties(value = {"user","product","wxPayOrder"})
@Table(name = "wx_pay_order")
@Data
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 根据一系列参数设计的订单号
     */
    @Column(nullable = false)
    private String orderNumber;

    /**
     * 提交该订单的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * 该订单的产品； 定义名为product_id的外键列，该外键引用product表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Product.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    public Product product;

    /**
     * 订购的产品数量
     */
    @Column(nullable = false)
    private Integer productQuantity;

    /**
     * 订单总价格
     */
    @Column(nullable = false)
    private Double totalPrice;

    /**
     * 用户提交的联系电话（考虑到用户自身没有绑定手机号码）
     */
    @Column(nullable = false)
    private String userPhone;

    /**
     * 绑定该订单的支付订单； 定义名为pay_order_id的外键列，该外键引用wx_pay_order表的主键(id)列,采用懒加载
     */
    @OneToOne(targetEntity = WxPayOrder.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_order_id")
    private WxPayOrder wxPayOrder;

    /**
     * 寄发快递的物流号
     */
    private String logisticsNumber;

    /**
     * 用户的备注
     */
    private String remark;

    /**
     * 支付订单的创建时间
     */
    @Column(nullable = false)
    private Date createTime;

    /**
     * 订单状态，“0”表示待付款，“1”表示已付款，“-1”表示订单已取消
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 订单完成后用户的评价
     */
    @Column(nullable = false)
    private String comment;

    public ProductInfo getProductInfo(){
        return new ProductInfo(product);
    }
}