package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.model.HairstylistInfo;
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
@JsonIgnoreProperties(value = { "handler", "hibernateLazyInitializer", "fieldHandler"})
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
     * 该文章中的图片列表； 定义该Product实体所有关联的ProductImageUrl实体； 指定mappedBy属性表明该Product实体不控制关联关系
     */
    @OneToMany(targetEntity = ProductImageUrl.class, mappedBy = "product")
    public List<ProductImageUrl> productImageUrlList;






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