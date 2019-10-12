package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.RecordToUserImgUrl;

import java.util.Date;
import java.util.List;

/**
 * 用于“发型师-预约列表-备注信息”页面
 */
public class NoteToOneUser {

    /**创建时间*/
    private Date createTime;

    /**备注内容*/
    private String content;

    /**该备注中的图片url列表*/
    private List<String> ImageUrlList;

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
        this.content = content;
    }

    public List<String> getImageUrlList() {
        return ImageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        ImageUrlList = imageUrlList;
    }
}
