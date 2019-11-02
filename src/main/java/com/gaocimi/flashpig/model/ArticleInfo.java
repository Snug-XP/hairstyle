package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Article;
import lombok.Data;

@Data
public class ArticleInfo {
    private Integer articleId;
    private String title;
    private String[] tag;

    public ArticleInfo(Article article) {
        this.articleId = article.getId();
        this.title = article.getTitle();
        this.tag = article.getTag();
    }
}
