package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * RecordToUserImgUrl - ‘发型师对顾客的备注中的图片url’实体类
 *
 * @author xp
 * @date 2019-10-11 19:46:27
 */
@Entity
@Table(name = "record_to_user_img_url")
@JsonIgnoreProperties(value = {"recordToUser"})//,"handler","hibernateLazyInitializer"})
public class RecordToUserImgUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**对应的备注类； 定义名为record_id的外键列，该外键引用record_hairstylis_to_user表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = RecordHairstylisToUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    public RecordHairstylisToUser recordToUser;

    /**备注信息中图片的url*/
    private String imageUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RecordHairstylisToUser getRecordToUser() {
        return recordToUser;
    }

    public void setRecordToUser(RecordHairstylisToUser recordToUser) {
        this.recordToUser = recordToUser;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}