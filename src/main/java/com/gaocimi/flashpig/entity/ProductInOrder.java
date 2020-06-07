package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * ProductInOrder - 商品订单的商品记录类
 *
 * @author xp
 * @date 2020-5-31 09:23:03
 */
@Entity
@Table(name = "product_in_order")
@JsonIgnoreProperties(value = {"id", "product", "productOrder", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class ProductInOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 商品图片
     */
    private String imgUrl;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品介绍
     */
    private String introduction;

    /**
     * 商品单价
     */
    private Double unitPrice;

    /**
     * 商品数量
     */
    private Integer productQuantity;

    /**
     * 该记录所属的商品订单类； 定义名为order_id的外键列，该外键引用product_order表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = ProductOrder.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    public ProductOrder productOrder;

    /**
     * 该记录中的商品类； 定义名为product_id的外键列，该外键引用product表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Product.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    public Product product;

    public ProductInOrder() {
        super();
    }

    public ProductInOrder(ProductOrder productOrder, Product product, Integer num) {
        this.productOrder = productOrder;
        this.product = product;
        this.productQuantity = num;
        setProductInfo(product);
    }

    public void setProductInfo(Product p) {
        if (p.productImageUrlList.size() > 0)
            this.imgUrl = p.productImageUrlList.get(0).getImageUrl();
        this.name = p.getName();
        this.introduction = p.getIntroduction();
        this.unitPrice = p.getPrice();
    }

}