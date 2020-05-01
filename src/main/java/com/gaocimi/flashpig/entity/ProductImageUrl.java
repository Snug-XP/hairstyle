package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;


/**
 * ProductImageUrl - 商品的图片url类
 *
 * @author xp
 * @date 2020-4-26 13:50:56
 */
@Entity
@Table(name = "product_image_url")
@JsonIgnoreProperties(value = {"product"})
@Data
public class ProductImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 上传该图片的商品； 定义名为product_id的外键列，该外键引用product表的主键(id)列,采用懒加载 */
    @ManyToOne(targetEntity = Product.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    /**商品图片的url*/
    @Column(nullable = false,unique = true)
    private String imageUrl;

}