package com.gaocimi.flashpig.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * RecordHairstylisToUser - ‘不知道什么’类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@Table(name = "record_hairstylis_to_user")
public class RecordHairstylisToUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date createTime;

    private String content;

    private String imageList;

    private Integer createId;

    private Integer userId;

    public RecordHairstylisToUser(Integer id, Date createTime, String content, String imageList, Integer createId, Integer userId) {
        this.id = id;
        this.createTime = createTime;
        this.content = content;
        this.imageList = imageList;
        this.createId = createId;
        this.userId = userId;
    }

    public RecordHairstylisToUser() {
        super();
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getImageList() {
        return imageList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList == null ? null : imageList.trim();
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}