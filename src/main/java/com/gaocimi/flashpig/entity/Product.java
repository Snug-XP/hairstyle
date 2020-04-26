package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Product - 商品类
 *
 * @author xp
 * @date 2020-4-26 13:50:40
 */
@Entity
@Table(name = "product")
@JsonIgnoreProperties(value = { "administrator","handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品的标签、关键词(以英文逗号隔开)
     */
    private String tag;

    /**
     * 商品介绍
     */
    private String introduction;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 商品的已售数量
     */
    private Integer sales;

    /**
     * 商品的剩余数量
     */
    private Integer remainingQuantity;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 发布该商品的管理员； 定义名为administrator_id的外键列，该外键引用administrator表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Administrator.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    public Administrator administrator;
    
    
    
    /**
     * 该文章中的图片列表； 定义该Product实体所有关联的ProductImageUrl实体； 指定mappedBy属性表明该Product实体不控制关联关系
     */
    @OneToMany(targetEntity = ProductImageUrl.class, mappedBy = "product")
    public List<ProductImageUrl> productImageUrlList;

    
    
    

    public Product() {
        sales = 0;
        remainingQuantity = 0;
        createTime = new Date(System.currentTimeMillis());
    }

    public String[] getTag() {
        if (tag == null)
            return null;
        return tag.split(",");
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTag(List<String> tagList) {
        this.tag = "";
        for (String tag : tagList) {
            this.tag += "," + tag.trim();
        }
        if (this.tag.length() > 0)
            this.tag = this.tag.substring(1);
    }

    public void addTag(List<String> tagList) {
        for (String tag : tagList) {
            if (this.tag == null || this.tag.length() == 0)
                this.tag = tag.trim();
            else
                this.tag += "," + tag.trim();
        }
    }

}