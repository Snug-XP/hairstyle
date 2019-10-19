package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * UserToHairstylist - 用户收藏发型师的记录类
 *
 * @author xp
 * @date 2019-10-18 19:33:52
 */
@Entity
@Table(name = "user_to_hairstylist")
@JsonIgnoreProperties(value = {"user","hairstylist"})
public class UserToHairstylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date createTime;

    /**提交该收藏记录的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**该收藏记录对应的发型师； 定义名为hairstylist_id的外键列，该外键引用hairstylist表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstylist_id", nullable = false)
    private Hairstylist hairstylist;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hairstylist getHairstylist() {
        return hairstylist;
    }

    public void setHairstylist(Hairstylist hairstylist) {
        this.hairstylist = hairstylist;
    }
}