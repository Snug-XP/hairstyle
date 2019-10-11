package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;


/**
 * User - 用户订单类
 *
 *@author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "user_order")
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**提交该订单的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**该订单对应的发型师； 定义名为hairstylist_id的外键列，该外键引用hairstylist表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstylist_id", nullable = false)
    public Hairstylist hairstylist;

    public Integer articleId;

    /**该订单选取的服务； 定义名为service_id的外键列，该外键引用hair_service表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = HairService.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    public HairService HairService;


    /**订单创建时间*/
    @Column(nullable = false)
    private Date createTime;

    /**订单价格*/
    @Column(nullable = false)
    private Double orderPrice;

    /**订单类型 1普通预约 2文章订阅*/
    private Integer type;

    /**订单状态 “0”表示订单正在进行中，”1“表示已完成，”-1“表示订单已取消，”-2“表示订单被拒绝*/
    @Column(nullable = false)
    private Integer state;

    /**预约时间*/
    @Column(nullable = false)
    private Date bookTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hairstylist getHairstylist() {
        return hairstylist;
    }

    public void setHairstylist(Hairstylist hairstylist) {
        this.hairstylist = hairstylist;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public HairService getHairService() {
        return HairService;
    }

    public void setHairService(com.gaocimi.flashpig.entity.HairService hairService) {
        HairService = hairService;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getBookTime() {
        return bookTime;
    }

    public void setBookTime(Date bookTime) {
        this.bookTime = bookTime;
    }
}