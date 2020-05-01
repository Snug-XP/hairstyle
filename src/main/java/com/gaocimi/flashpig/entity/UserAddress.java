package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


/**
 * UserAddress - 用户保存的配送地址
 *
 * @author xp
 * @date 2020-5-1 11:35:38
 */
@Entity
@JsonIgnoreProperties(value = {"user"})
@Table(name = "user_address")
@Data
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 提交该配送地址的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * 配送省份
     */
    private String province;

    /**
     * 配送城市
     */
    private String city;

    /**
     * 配送区县
     */
    private String district;

    /**
     * 配送详细地址(村、路、门牌)
     */
    private String address;

    public String toString() {
        return province + " " + city + " " + district + " " + address;
    }
}