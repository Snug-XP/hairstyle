package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.entity.ArticleImageUrl;
import lombok.Data;

import java.util.List;

@Data
public class ArticleInfo {
    private Integer articleId;
    private String imgUrl;
    private String title;
    private String[] tag;

    public ArticleInfo(Article article) {
        this.articleId = article.getId();
        this.title = article.getTitle();
        this.tag = article.getTag();

        List<ArticleImageUrl> imgList = article.getArticleImageUrlList();
        if (imgList != null && imgList.size() > 0)
            this.imgUrl = imgList.get(0).getImageUrl();
    }
    public ArticleInfo() {
        super();
    }
}
