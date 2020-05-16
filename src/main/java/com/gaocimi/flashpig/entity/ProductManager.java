package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * ProductManager - 商品管理员账户类
 *
 * @author xp
 * @date 2020-5-16 09:20:08
 */
@Entity
@Table(name = "product_manager")
@JsonIgnoreProperties(value = {"customerAnalyzeData", "timeRate", "loyalCustomerList", "customerList", "maleAndFemaleInfo", "mostPopularPerson", "maxOrderPerson", "maxPointPerson", "point", "applyTime", "applyResultDescription", "openid", "password", "hairstylistsByStatus", "hairstylists", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class ProductManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 记录本次登录的微信的openid
     */
    @Column(name = "temp_openid")
    private String openid;

    /**
     * 商品管理员名称
     */
    private String name;

    /**
     * 商品管理员联系电话（也是登录账号）
     */
    private String phone;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 商品管理员账户被创建的时间
     */
    private Date createTime;

    /**
     * 商品管理员创建的商品列表； 定义该ProductManager实体所有关联的Product实体； 指定mappedBy属性表明该ProductManager实体不控制关联关系
     */
    @OneToMany(targetEntity = Product.class, mappedBy = "productManager")
    public List<Product> productList;

    //初始化
    public ProductManager() {
        Date date = new Date(System.currentTimeMillis());
        setCreateTime(date);//设置注册时间
     }
}