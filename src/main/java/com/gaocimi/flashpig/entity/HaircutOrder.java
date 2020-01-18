package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * HaircutOrder - 用户的理发订单类
 *
 * @author xp
 * @date 2019-9-23 03:14:25
 */
@Entity
@JsonIgnoreProperties(value = {"article"})
@Table(name = "haircut_order")
@Data
public class HaircutOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 该订单中按照一些参数生成的预约号
     */
    @Column(nullable = false)
    private String reservationNum;

    /**
     * 该订单中用户提交的联系电话（考虑到用户自身没有绑定手机号码）
     */
    @Column(nullable = false)
    private String userPhone;

    /**
     * 提交该订单的用户； 定义名为user_id的外键列，该外键引用user表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**
     * 该订单中用户选择的文章； 定义名为article_id的外键列，该外键引用article表的主键(id)列,采用懒加载
     */
    @ManyToOne(targetEntity = Article.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
    public Article article;

    /**
     * 该订单对应的发型师； 定义名为hairstylist_id的外键列，该外键引用hairstylist表的主键(id)列,采用急加载
     */
    @ManyToOne(targetEntity = Hairstylist.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "hairstylist_id", nullable = false)
    public Hairstylist hairstylist;

    /**
     * 该订单选取的服务项目名称
     */
    @Column(nullable = false)
    private String serviceName;

    /**
     * 该订单选取的服务项目描述
     */
    @Column(nullable = false)
    private String description;

    /**
     * 该订单选取的服务项目大致价格
     */
    @Column(nullable = false)
    private Double price;

    /**
     * 订单创建时间
     */
    @Column(nullable = false)
    private Date createTime;

    /**
     * 预约时间
     */
    @Column(nullable = false)
    private Date bookTime;

    /**
     * 订单状态，“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 订单完成后用户的评价
     */
    @Column(nullable = true)
    private String comment;

    /**
     * 订单完成后用户的评分
     */
    @Column(nullable = true)
    private Double point;

    public Integer getId() {
        return id;
    }

    public String getAddress() {
        return hairstylist.shop.getCity() + " " + hairstylist.shop.getDistrict() + " " + hairstylist.shop.getAddress();
    }

    public List<ArticleImageUrl> getArticleImageUrlList() {
        if (article == null)
            return null;
        else
            return article.articleImageUrlList;
    }
}