package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.ArticleService;
import com.gaocimi.flashpig.service.UserService;
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
    UserService userService;

    @ApiOperation(value = "添加文章")
    @PostMapping("/article")
    public int addArticle(@Validated Article articles) {
        articleService.save(articles);
        return 200;
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


    @ApiOperation(value = "普通用户分页获取自己收藏的文章列表")
    @GetMapping("/article/getMyCollection")
    public Map getMyCollectionByPage( String myOpenid,
                                    @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            List<Article> tempArticleList = user.articleList;
            List<Article> resultArticleList = new ArrayList<>();

            if(tempArticleList==null){
                logger.info("你还没有收藏文章哦~");
                map.put("message","你还没有收藏文章哦~");
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
            int first = pageNum*pageSize;
            int last = pageNum*pageSize+pageSize-1;
            for(int i = first ; i<=last&&i<tempArticleList.size() ; i++){
                resultArticleList.add(tempArticleList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum,pageSize);
            Page<Article> page = new PageImpl<Article>(resultArticleList,pageable,tempArticleList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("获取自己收藏的文章列表失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取收藏列表失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }
}
