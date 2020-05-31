package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * ProductOrder - 商品订单类
 *
 * @author xp
 * @date 2020-5-2 10:54:07
 */
@Entity
@JsonIgnoreProperties(value = {"user", "wxPayOrder", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Table(name = "product_order")
@Data
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 根据一系列参数设计的订单号(用户id+产品id+时间串)
     */
    @Column(nullable = false, unique = true)
    private String orderNumber;

    /**
     * 提交该订单的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * 该订单中包含的商品记录列表； 定义该ProductOrder实体所有关联的ProductInOrder实体； 指定mappedBy属性表明该ProductOrder实体不控制关联关系
     */
    @OneToMany(targetEntity = ProductInOrder.class, mappedBy = "productOrder", fetch = FetchType.EAGER)
    public List<ProductInOrder> productRecordList;

    /**
     * 订单总价格
     */
    @Column(nullable = false)
    private Double totalPrice;

    /**
     * 用户提交的收件人姓名
     */
    @Column(nullable = false)
    private String userName;

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
     * 配送省份
     */
    @Column(nullable = false)
    private String province;

    /**
     * 配送城市
     */
    @Column(nullable = false)
    private String city;

    /**
     * 配送区县
     */
    @Column(nullable = false)
    private String district;

    /**
     * 配送详细地址(村、路、门牌)
     */
    @Column(nullable = false)
    private String address;

    public ProductOrder() {
        status = 0;
        createTime = new Date();
    }

    /**
     * 生成订单号：用户id+产品id+时间串
     */
    public void generateOrderNumber() {
        orderNumber = user.getId() + "0" + MyUtils.getTimeStringInteger(new Date());
    }

    /**
     * 填入配送地址
     */
    public void setDeliveryAddress(UserAddress address) {
        this.userName = address.getName();
        this.userPhone = address.getPhone();
        this.province = address.getProvince();
        this.city = address.getCity();
        this.district = address.getDistrict();
        this.address = address.getAddress();
    }

    /**
     * 计算订单总价
     */
    public void calculateTotalPrice() {
        totalPrice = 0.0;
        for (ProductInOrder record : productRecordList) {
            totalPrice += record.getUnitPrice() * record.getProductQuantity();
        }
    }
}