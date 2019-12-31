package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.model.HairstylistInfo;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Feedback - 用户反馈类
 *
 * @author xp
 * @date 2019-12-31 17:01:02
 */
@Entity
@Table(name = "feedback")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 提交该反馈的用户openid
     */
    private String openid;

    /**
     * 反馈的内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

}