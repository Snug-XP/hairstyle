package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.repository.ArticleRepository;
import com.gaocimi.flashpig.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");  //按创建时间倒序
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

    @Override
    public List<Article> findAllByTagLikeAndStatus(List<String> tagList,Integer status) {
        Set<Article> set = new HashSet<>();//使用集合Set，自带去重功能

        for (String tag : tagList) {
            List<Article> tempList = articleRepository.findAllByTagLikeAndStatus("%" + tag + "%" , status);
            set.addAll(tempList);
        }
        //这样合并去重需要重写对象的equals()方法,但是发现不重写也可以
//            articleList.removeAll(tempList);
//            articleList.addAll(tempList);

        return new ArrayList<>(set);
    }

    @Override
    public List<Article> findAllByTitleLike(String title) {
        return articleRepository.findAllByTitleLike("%" + title + "%");
    }

    @Override
    public List<Article> findAllByContentLike(String content) {
        return articleRepository.findAllByContentLike("%" + content + "%");
    }


    /**
     * 分页获取待审核或者审核通过的文章的发型文章
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public Page<Article> findAllByStatus(int status, int pageNum, int pageSize) {
        int first = pageNum * pageSize;
        int last = pageNum * pageSize + pageSize - 1;

        List<Article> articles = articleRepository.findAllByStatus(status);
        List<Article> resultList = new ArrayList<>();

        for (int i = first; i <= last && i < articles.size(); i++) {
            resultList.add(articles.get(i));
        }

        //包装分页数据
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Article> page = new PageImpl<>(resultList, pageable, articles.size());

        return page;
    }
}


