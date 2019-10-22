package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * UserToHairstylist - 用户收藏发型师的中间表记录类
 *
 * @author xp
 * @date 2019-10-18 19:33:52
 */
@Entity
@Table(name = "user_to_hairstylist")
@JsonIgnoreProperties(value = {"user", "hairstylist", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class UserToHairstylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date createTime;

    /**
     * 提交该收藏记录的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**
     * 该收藏记录对应的发型师； 定义名为hairstylist_id的外键列，该外键引用hairstylist表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstylist_id", nullable = false)
    public Hairstylist hairstylist;

    public UserToHairstylist(User user, Hairstylist hairstylist) {
        this.user = user;
        this.hairstylist = hairstylist;
        this.createTime = new Date(System.currentTimeMillis());
    }

    public UserToHairstylist() {
        super();
    }
}