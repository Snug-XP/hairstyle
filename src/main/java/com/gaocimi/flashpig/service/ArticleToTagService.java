package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ArticleToTag;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-11-6 13:34:49
 * @description 对数据库article_toTag表进行相关操作
 */
public interface ArticleToTagService {

    public List<ArticleToTag> getArticleToTagList();

    public ArticleToTag findArticleToTagById(int id);

    public void save(ArticleToTag articleToTag);

    public void edit(ArticleToTag articleToTag);

    public void delete(int id);

    public Page<ArticleToTag> findAll(int pageNum, int pageSize);

    public boolean existsByArticleIdAndTagId(int articleId, int tagId);

}
