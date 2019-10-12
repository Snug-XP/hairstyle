package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

/**
 * User - 用户类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = {"haircutOrderList","handler","hibernateLazyInitializer"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String pictureUrl;

    private Integer sex;

    private String openid;

    private Integer type;

    private String phoneNum;

    private Integer state;

    private String province;

    private String city;

    private String collections;

    /**用户提交过的订单列表； 定义该User实体所有关联的HaircutOrder实体； 指定mappedBy属性表明该User实体不控制关联关系*/
    @OneToMany(targetEntity = HaircutOrder.class, mappedBy = "user")
    public List<HaircutOrder> haircutOrderList;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPictureUrl() {  return pictureUrl;  }

    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum == null ? null : phoneNum.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getCollections() {
        return collections;
    }

    public void setCollections(String collections) { this.collections = collections == null ? null : collections.trim();}

    public List<HaircutOrder> getHaircutOrderList() { return haircutOrderList; }

    public void setHaircutOrderList(List<HaircutOrder> haircutOrderList) { this.haircutOrderList = haircutOrderList; }


}