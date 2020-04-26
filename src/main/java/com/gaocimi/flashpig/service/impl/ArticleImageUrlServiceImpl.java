package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.ArticleImageUrl;
import com.gaocimi.flashpig.repository.ArticleImageUrlRepository;
import com.gaocimi.flashpig.service.ArticleImageUrlService;
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
 * @date 2019-10-21 20:16:20
 * @description 对数据库article_image_url表进行相关操作的实现类
 */
@Service
public class ArticleImageUrlServiceImpl implements ArticleImageUrlService {
    protected static final Logger logger = LoggerFactory.getLogger(ArticleImageUrlServiceImpl.class);

    @Autowired
    public ArticleImageUrlRepository articleImageUrlRepository;

    @Override
    public List<ArticleImageUrl> getArticleImageUrlList() {
        return articleImageUrlRepository.findAll();
    }

    @Override
    public ArticleImageUrl findArticleImageUrlById(int id) {
        return articleImageUrlRepository.findById(id);
    }

    @Override
    public void save(ArticleImageUrl articleImageUrl) {
        articleImageUrlRepository.save(articleImageUrl);
    }

    @Override
    public void edit(ArticleImageUrl articleImageUrl) {
        articleImageUrlRepository.save(articleImageUrl);
    }

    @Override
    public void delete(int id) {
        articleImageUrlRepository.deleteById(id);
    }

    @Override
    public void deleteAllByArticleId(int articleId){
        articleImageUrlRepository.deleteAllByArticle_Id(articleId);
    }



    public List<ArticleImageUrl> findAll()
    {
        return articleImageUrlRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<ArticleImageUrl> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<ArticleImageUrl> articleImageUrlPage = null;
        try {
            articleImageUrlPage = articleImageUrlRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return articleImageUrlPage;
    }
}


