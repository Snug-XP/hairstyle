package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * ShopImageUrl - 门店的场景图片url类
 *
 * @author xp
 * @date 2019-11-17 20:55:25
 */
@Entity
@Table(name = "shop_image_url")
@JsonIgnoreProperties(value = {"shop"})
@Data
public class ShopImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 上传该作品图片的门店； 定义名为shop_id的外键列，该外键引用shop表的主键(id)列,采用懒加载 */
    @ManyToOne(targetEntity = Shop.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    /**门店作品图片的url*/
    @Column(nullable = false,unique = true)
    private String imageUrl;
}