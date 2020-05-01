//package com.gaocimi.flashpig.service.impl;
//
//import com.gaocimi.flashpig.entity.ArticleToTag;
//import com.gaocimi.flashpig.repository.ArticleToTagRepository;
//import com.gaocimi.flashpig.service.ArticleToTagService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author xp
// * @date 2019-10-18 19:43:22
// * @description 对数据库user_to_hairstylist表进行相关操作的实现类
// */
//@Service
//public class ArticleToTagServiceImpl implements ArticleToTagService {
//    protected static final Logger logger = LoggerFactory.getLogger(ArticleToTagServiceImpl.class);
//
//    @Autowired
//    public ArticleToTagRepository articleToTagRepository;
//
//    @Override
//    public List<ArticleToTag> getArticleToTagList() {
//        return articleToTagRepository.findAll();
//    }
//
//    @Override
//    public ArticleToTag findArticleToTagById(int id) {
//        return articleToTagRepository.findById(id);
//    }
//
//    @Override
//    public boolean existsByArticleIdAndTagId(int articleId, int tagId){
//        return articleToTagRepository.existsByArticleIdAndTagId(articleId,tagId);
//    }
//
//    @Override
//    public void save(ArticleToTag articleToTag) {
//        articleToTagRepository.save(articleToTag);
//    }
//
//    @Override
//    public void edit(ArticleToTag articleToTag) {
//        articleToTagRepository.save(articleToTag);
//    }
//
//    @Override
//    public void delete(int id) {
//        articleToTagRepository.deleteById(id);
//    }
//
//
//    public List<ArticleToTag> findAll() {
//        return articleToTagRepository.findAll();
//    }
//
//    // 分页获得列表
//    @Override
//    public Page<ArticleToTag> findAll(int pageNum, int pageSize) {
//        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
//        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
//
//        Page<ArticleToTag> articleToTagPage = null;
//        try {
//            articleToTagPage = articleToTagRepository.findAll(pageable);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("查询记录出错");
//            return null;
//        }
//        return articleToTagPage;
//    }
//}
//
//
