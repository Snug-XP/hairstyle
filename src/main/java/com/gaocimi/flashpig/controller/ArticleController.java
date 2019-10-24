package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author xp
 * @date 2019-10-14 15:03:39
 * @description 文章相关业务
 */
@RestController
@ResponseResult
@Api(value = "文章服务相关业务", description = "文章相关业务")
public class ArticleController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    ArticleService articleService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserService userService;
    @Autowired
    UserToArticleService userToArticleService;
    @Autowired
    ArticleImageUrlService imageUrlService;
    @Autowired
    AdministratorService administratorService;

    @ApiOperation(value = "添加文章")
    @PostMapping("/article")
    public Map addArticle(String myOpenid ,String title, String content,
                          @RequestParam(value = "tagList", required = false) List<String> tagList,
                          @RequestParam(value = "imgUrlList", required = false) List<String> imgUrlList) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }

            Article article = new Article();
            article.setHairstylist(hairstylist);
            article.setTag(tagList);
            article.setTitle(title);
            article.setContent(content);
            article.setCreateTime(new Date(System.currentTimeMillis()));
            articleService.save(article);

            for (String imageUrlStr : imgUrlList) {
                ArticleImageUrl imageUrl = new ArticleImageUrl();
                imageUrl.setArticle(article);
                imageUrl.setImageUrl(imageUrlStr);

                imageUrlService.save(imageUrl);
            }
            map.put("message", "文章上传成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("文章上传失败！！（后端发生某些错误）");
            map.put("error", "文章上传失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "删除文章")
    @DeleteMapping("/article/{articleId}")
    public int deleteArticle(@PathVariable("articleId") Integer articleId) {
        articleService.delete(articleId);
        return 200;
    }

    @ApiOperation(value = "修改文章")
    @PutMapping("/article")
    public int updateArticle(@Validated Article articles) {
        articleService.edit(articles);
        return 200;
    }


    @ApiOperation(value = "获取单个文章信息", produces = "application/json")
    @GetMapping("/article/getOne/{articleId}")
    public Article getOne(@PathVariable("articleId") Integer articleId) {
        return articleService.findArticleById(articleId);
    }

    @ApiOperation(value = "获取所有文章列表")
    @GetMapping("/articles/getAll")
    public Page<Article> getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<Article> page = articleService.findAll(pageNum, pageSize);
        return page;
    }

    @ApiOperation(value = "收藏该文章")
    @PostMapping("/article/addToCollection")
    public Map addToCollection(String myOpenid, int articleId) {
        Map map = new HashMap();
        try {
            User user = userService.findUserByOpenid(myOpenid);
            Article article = articleService.findArticleById(articleId);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (article == null) {
                logger.info("id为" + articleId + "的文章不存在！");
                map.put("error", "该文章不存在！！");
                return map;
            }
            if (userToArticleService.findByUserAndArticle(user.getId(), articleId) != null) {
                logger.info("该用户已收藏该文章，不需要重复收藏");
                map.put("message", "已收藏该文章!!");
                return map;
            }
            UserToArticle userToArticle = new UserToArticle(user, article);
            userToArticleService.save(userToArticle);
            logger.info("id为" + user.getId() + "的用户收藏了id为" + article.getId() + "的文章（title：" + article.getTitle() + "）");
            map.put("message", "收藏成功！");

        } catch (Exception e) {
            logger.info("后端发生异常：\n");
            logger.error(e.getMessage());
            map.put("error", "抱歉，后端发生异常!!");
        }

        return map;
    }


    @ApiOperation(value = "普通用户分页获取自己收藏的文章列表")
    @GetMapping("/article/getMyCollection")
    public Map getMyCollectionByPage(String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            List<UserToArticle> tempArticleList = user.getArticleRecordList();
            List<Article> resultArticleList = new ArrayList<>();

            if (tempArticleList == null) {
                logger.info("你还没有收藏文章哦~");
                map.put("message", "你还没有收藏文章哦~");
                return map;
            }

            // 按时间倒序排序
            Collections.sort(tempArticleList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            //获取所求页数的文章数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempArticleList.size(); i++) {
                resultArticleList.add(tempArticleList.get(i).article);
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Article> page = new PageImpl<>(resultArticleList, pageable, tempArticleList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己收藏的文章列表失败！！（后端发生某些错误）");
            map.put("error", "获取收藏列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }
}
