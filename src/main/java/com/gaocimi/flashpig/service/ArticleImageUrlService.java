package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ArticleImageUrl;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-21 20:15:14
 * @description 对数据库article_image_url表进行相关操作
 */
public interface ArticleImageUrlService {

    public List<ArticleImageUrl> getArticleImageUrlList();

    public ArticleImageUrl findArticleImageUrlById(int id);

    public void save(ArticleImageUrl articleImageUrl);

    public void edit(ArticleImageUrl articleImageUrl);

    public void delete(int id);

    public void deleteAllByArticlId(int articleId);

    public Page<ArticleImageUrl> findAll(int pageNum, int pageSize);
}
