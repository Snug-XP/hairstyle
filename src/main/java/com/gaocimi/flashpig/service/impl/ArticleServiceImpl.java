package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.repository.ArticleRepository;
import com.gaocimi.flashpig.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xp
 * @date 2019-9-23 01:24:31
 * @description 对数据库article表进行相关操作的实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    protected static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    public ArticleRepository articleRepository;

    @Override
    public List<Article> getArticleList() {
        return articleRepository.findAll();
    }

    @Override
    public Article findArticleById(int id) {
        return articleRepository.findById(id);
    }

    @Override
    public void save(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void edit(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void delete(int id) {
        articleRepository.deleteById(id);
    }


    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<Article> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Article> articlePage = null;
        try {
            articlePage = articleRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return articlePage;
    }
}


