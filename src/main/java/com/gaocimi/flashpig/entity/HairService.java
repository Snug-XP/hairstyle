package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * HairService - 理发服务类
 *
 * @author xp
 * @date 2019-9-23 03:08:43
 */
@Entity
@Table(name = "hair_service")
@JsonIgnoreProperties(value = {"hairstylist"})
public class HairService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**设置该服务的发型师； 定义名为hairstylist_id的外键列，该外键引用hairstylist表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstylist_id", nullable = false)
    public Hairstylist hairstylist;

    /**服务名称*/
    @Column(nullable = false)
    private String serviceName;

    /**该服务的价格*/
    @Column(nullable = false)
    private Double price;

    /**服务具体描述*/
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Hairstylist getHairstylist() {
        return hairstylist;
    }

    public void setHairstylist(Hairstylist hairstylist) {
        this.hairstylist = hairstylist;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}