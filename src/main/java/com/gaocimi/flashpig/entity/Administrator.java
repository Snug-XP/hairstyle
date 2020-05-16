package com.gaocimi.flashpig.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Administrator - 管理员类
 *
 * @author xp
 * @date 2019-10-10 15:22:53
 */
@Entity
@Table(name = "administrator")
@JsonIgnoreProperties(value = {"productList","albumList", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String openid;

    /**
     * 该管理员创建的专辑列表； 定义该Administrator实体所有关联的Album实体； 指定mappedBy属性表明该Administrator实体不控制关联关系
     */
    @OneToMany(targetEntity = Album.class, mappedBy = "administrator")
    public List<Album> albumList;
    }