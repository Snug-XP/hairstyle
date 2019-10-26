package com.gaocimi.flashpig.service;

import java.util.List;
import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.entity.Article;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:22:03
 * @description 对数据库article表进行相关操作
 */
public interface ArticleService {

    public List<Article> getArticleList();

    public Article findArticleById(int id);

    public void save(Article article);

    public void edit(Article article);

    public void delete(int id);

    public Page<Article> findAll(int pageNum,int pageSize);

    /**
     * 分页获取待审核的发型文章
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    public Page<Article> findPendingList(int pageNum, int pageSize);
}
