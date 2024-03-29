package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * ArticleImageUrl - 文章的图片url类
 *
 * @author xp
 * @date 2019-10-21 20:05:19
 */
@Entity
@Table(name = "article_image_url")
@JsonIgnoreProperties(value = {"article"})
@Data
public class ArticleImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 上传该图片的文章； 定义名为article_id的外键列，该外键引用article表的主键(id)列,采用懒加载 */
    @ManyToOne(targetEntity = Article.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    public Article article;

    /**文章图片的url*/
    @Column(nullable = false,unique = true)
    private String imageUrl;

}