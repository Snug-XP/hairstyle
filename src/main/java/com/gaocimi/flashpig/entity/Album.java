package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Album - 专辑类
 *
 * @author xp
 * @date 2019-10-24 21:07:37
 */
@Entity
@Table(name = "album")
@JsonIgnoreProperties(value = {"articleList", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 标签、关键词(以英文逗号隔开)
     */
    private String tag;

    /**
     * 专辑的（头像）图片url
     */
    private String imgUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**创建该专辑的管理员； 定义名为admin_id的外键列，该外键引用administrator表的主键(id)列,采用懒加载*/
    @ManyToOne(targetEntity = Administrator.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    public Administrator administrator;

    /**该专辑中包含的发型文章*/
    @JoinTable(name="article_to_album",
            joinColumns={@JoinColumn(name="album_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="article_id", referencedColumnName="id")})
    @ManyToMany(fetch = FetchType.LAZY)
    public List<Article> articleList;


    public String[] getTag() {
        if(tag==null)
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

    public boolean existArticle(int articleId){
        for (Article a : articleList) {
            if (a.getId() == articleId) {
                return true;
            }
        }
        return false;
    }

}