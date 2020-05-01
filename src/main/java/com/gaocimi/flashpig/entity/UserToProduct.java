package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * UserToProduct - 用户收藏商品的中间表记录类
 *
 * @author xp
 * @date 2020-4-27 15:37:22
 */
@Entity
@Table(name = "user_to_product")
@JsonIgnoreProperties(value = {"user", "product", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class UserToProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date createTime;

    /**
     * 提交该收藏记录的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**
     * 该收藏记录对应的商品； 定义名为product_id的外键列，该外键引用product表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Product.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    public UserToProduct(User user, Product product) {
        this.user = user;
        this.product = product;
        this.createTime = new Date(System.currentTimeMillis());
    }

    public UserToProduct() {
        super();
        this.createTime = new Date(System.currentTimeMillis());
    }
}