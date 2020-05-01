package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.ArticleInfo;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
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
    @Autowired
    AlbumService albumService;
//    @Autowired
//    ArticleToAlbumlbumService articleToAlbumlbumService;

    @ApiOperation(value = "添加发型文章")
    @PostMapping("/hairstylist/addArticle")
    public Map addArticle(@RequestParam String myOpenid, @RequestParam String title, @RequestParam String content,
                          @RequestParam(value = "tagList", required = false) List<String> tagList,
                          @RequestParam(value = "imgUrlList", required = false) List<String> imgUrlList) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null  && (!administratorService.isExist(myOpenid))) {
                logger.info("非发型师用户或管理员操作（添加文章）！！");
                map.put("error", "对不起，你不是发型师或管理员，无权操作！！");
                return map;
            }

            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（添加发型文章）！！");
                map.put("error", "对不起，你还没有入驻门店，不可操作！！");
                return map;
            }

            Article article = new Article();
            article.setHairstylist(hairstylist);
            article.setTag(tagList);
            article.setTitle(title);
            article.setContent(content);
            article.setCreateTime(new Date(System.currentTimeMillis()));
            if (administratorService.isExist(myOpenid))
                article.setStatus(1);//设置发型文章状态为审核通过
            else
                article.setStatus(0);//设置发型文章状态为审核中

            articleService.save(article);

            //储存发型文章的图片url列表
            if(imgUrlList!=null) {
                for (String imageUrlStr : imgUrlList) {
                    ArticleImageUrl imageUrl = new ArticleImageUrl();
                    imageUrl.setArticle(article);
                    imageUrl.setImageUrl(imageUrlStr);

                    imageUrlService.save(imageUrl);
                }
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

    @ApiOperation(value = "删除发型文章", notes = "权限：仅文章的发布者或管理员")
    @DeleteMapping("/article")
    public Map deleteArticle(@RequestParam String myOpenid, @RequestParam Integer articleId) {

        Map map = new HashMap();
        try {
            Article article = articleService.findArticleById(articleId);
            if (article == null) {
                logger.info("id为" + articleId + "的文章不存在（删除文章）！");
                map.put("error", "该文章不存在！！");
                return map;
            }

            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ((hairstylist == null) && (!administratorService.isExist(myOpenid))) {
                logger.info("非发型师用户或管理员操作（删除文章）！！");
                map.put("error", "对不起，你不是发型师或管理员，无权操作！！");
                return map;
            }


            if (!hairstylist.isMyArticle(articleId)) {
                logger.info("该文章不是该发型师创建的，无权删除！！");
                map.put("error", "该文章不是你创建的，无权删除！！");
                return map;
            }

            logger.info("id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”删除了id为" + articleId + "的文章（Title:" + article.getTitle() + "）");
            articleService.delete(articleId);
            map.put("message", "删除成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("文章删除失败！！（后端发生某些错误）");
            map.put("error", "文章删除失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "修改发型文章", notes = "权限：仅文章的发布者")
    @PutMapping("/hairstylist/updateArticle")
    public Map updateArticle(@RequestParam String myOpenid, @RequestParam Integer articleId, @RequestParam String title, @RequestParam String content,
                             @RequestParam(value = "tagList", required = false) List<String> tagList,
                             @RequestParam(value = "imgUrlList", required = false) List<String> imgUrlList) {
        Map map = new HashMap();
        try {

            Article article = articleService.findArticleById(articleId);

            if (article == null) {
                logger.info("id为" + articleId + "的文章不存在（修改文章）！");
                map.put("error", "该文章不存在！！");
                return map;
            }

            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null ) {
                logger.info("非发型师用户操作（修改文章）！！");
                map.put("error", "对不起，你不是发型师，无权操作！！");
                return map;
            }

            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（修改发型文章）！！");
                map.put("error", "对不起，你还没有入驻门店，不可操作！！");
                return map;
            }

            if (!hairstylist.isMyArticle(articleId)) {
                logger.info("该文章不是该发型师创建的，无权修改！！");
                map.put("error", "该文章不是你创建的，无权修改！！");
                return map;
            }

            article.setTag(tagList);
            article.setTitle(title);
            article.setContent(content);
            if (administratorService.isExist(myOpenid))
                article.setStatus(1);//设置发型文章状态为审核通过
            else
                article.setStatus(0);//设置发型文章状态为审核中
            articleService.edit(article);


            //删除原有文章的图片url
            imageUrlService.deleteAllByArticleId(articleId);

            //储存发型文章的图片url列表
            for (String imageUrlStr : imgUrlList) {
                ArticleImageUrl imageUrl = new ArticleImageUrl();
                imageUrl.setArticle(article);
                imageUrl.setImageUrl(imageUrlStr);

                imageUrlService.save(imageUrl);
            }
            logger.info("id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”修改了id为" + articleId + "的文章");
            map.put("message", "文章修改成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("文章修改失败！！（后端发生某些错误）");
            map.put("error", "文章修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取自己的发型文章列表(分页展示)", notes = "权限：发型师(管理员会有一个发型师记录)")
    @GetMapping("/hairstylist/getMyArticleList")
    public Map getMyArticleList(@RequestParam String myOpenid,
                                @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null ) {
                logger.info("非发型师用户或管理员操作（获取自己的发型文章列表）！！");
                map.put("error", "对不起，你不是发型师或管理员，无权操作！！");
                return map;
            }

            List<Article> tempArticleList = hairstylist.getArticleList();
            List<Article> resultList = new ArrayList<>();

            if (tempArticleList == null || tempArticleList.isEmpty()) {
                logger.info("你还没有创建过发型文章哦~");
                map.put("message", "你还没有创建过发型文章哦~");
                return map;
            }

            // 按创建时间倒序排序
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
                resultList.add(tempArticleList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Article> page = new PageImpl<>(resultList, pageable, tempArticleList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己的发型文章列表失败！！（后端发生某些错误）");
            map.put("error", "获取自己的发型文章列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取单个文章信息", produces = "application/json")
    @GetMapping("/article/getOne")
    public Map getOne(@RequestParam String myOpenid,@RequestParam Integer articleId) {
        Map map = new HashMap();
        try {
            User user = userService.findUserByOpenid(myOpenid);
            Article article = articleService.findArticleById(articleId);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(获取单个文章信息)");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (article == null) {
                logger.info("id为" + articleId + "的文章不存在！(获取单个文章信息)");
                map.put("error", "该文章不存在！！");
                return map;
            }
            if (userToArticleService.findByUserAndArticle(user.getId(), articleId) != null) {
                map.put("isCollected", "yes");
                map.put("article",article);
                return map;
            }else{
                map.put("isCollected", "no");
                map.put("article",article);
                return map;
            }

        } catch (Exception e) {
            logger.info("后端发生异常：\n");
            map.put("error", "抱歉，后端发生异常!!");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取所有文章列表（分页展示）")
    @GetMapping("/articles/getAll")
    public Page<Article> getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<Article> page = articleService.findAllByStatus(1, pageNum, pageSize);
        return page;
    }

    @ApiOperation(value = "收藏或取消收藏该发型文章（转换用户对发型文章的收藏关系）")
    @PostMapping("/article/addOrRemoveCollection")
    public Map addOrRemoveCollection(@RequestParam String myOpenid, @RequestParam Integer articleId) {
        Map map = new HashMap();
        try {
            User user = userService.findUserByOpenid(myOpenid);
            Article article = articleService.findArticleById(articleId);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(收藏或取消收藏该发型文章)");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (article == null) {
                logger.info("id为" + articleId + "的文章不存在！(收藏或取消收藏该发型文章)");
                map.put("error", "该文章不存在！！");
                return map;
            }
            UserToArticle userToArticle = userToArticleService.findByUserAndArticle(user.getId(), articleId);
            if ( userToArticle != null) {
                userToArticleService.delete(userToArticle.getId());
                logger.info("id为" + user.getId() + "的用户“"+user.getName()+"”取消收藏了id为" + article.getId() + "的文章（title：" + article.getTitle() + "）");
                map.put("message", "取消收藏成功！");
                return map;
            }
            userToArticle = new UserToArticle(user, article);
            userToArticleService.save(userToArticle);
            logger.info("id为" + user.getId() + "的用户“"+user.getName()+"”收藏了id为" + article.getId() + "的文章（title：" + article.getTitle() + "）");
            map.put("message", "收藏成功！");

        } catch (Exception e) {
            logger.info("后端发生异常：\n");
            logger.error(e.getMessage());
            map.put("error", "抱歉，后端发生异常!!");
        }

        return map;
    }


    @ApiOperation(value = "将某文章加入某专辑", notes = "权限：仅管理员")
    @PostMapping("/article/addToAlbum")
    public Map addToAlbum(@RequestParam String myOpenid, @RequestParam Integer articleId, @RequestParam Integer albumId) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作（将某文章加入某专辑）！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }
            Article article = articleService.findArticleById(articleId);
            if (article == null) {
                logger.info("id为" + articleId + "的文章不存在！（将某文章加入某专辑）");
                map.put("error", "该文章不存在！！");
                return map;
            }
            Album album = albumService.findAlbumById(albumId);
            if (album == null) {
                logger.info("id为" + albumId + "的专辑不存在！（将某文章加入某专辑）");
                map.put("error", "该专辑不存在！！");
                return map;
            }
            for (Article a : album.getArticleList()) {
                if (a.getId() == articleId) {
                    logger.info("该专辑已收录该发型文章，不需要重复收录");
                    map.put("message", "已收录该文章!!");
                    return map;
                }
            }
            List<Article> articleList = album.getArticleList();
            articleList.add(article);
            album.setArticleList(articleList);
            albumService.edit(album);

            logger.info("id为" + albumId + "的专辑（title：" + album.getTitle() + "）收录了id为" + articleId + "的文章（title：" + article.getTitle() + "）");
            map.put("message", "收录成功！");

        } catch (Exception e) {
            logger.info("后端发生异常\n");
            map.put("error", "抱歉，后端发生异常!!");
            e.printStackTrace();
        }

        return map;
    }


    @ApiOperation(value = "普通用户分页获取自己收藏的文章列表")
    @GetMapping("/article/getMyCollection")
    public Map getMyCollectionByPage(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(获取自己收藏的文章列表)");
                map.put("error", "无效的用户！！");
                return map;
            }
            List<UserToArticle> tempArticleList = user.getArticleRecordList();
            List<Article> resultArticleList = new ArrayList<>();

            if (tempArticleList == null||tempArticleList.isEmpty()) {
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


    @ApiOperation(value = "用户获取相关标签的文章列表")
    @GetMapping("/article/getAtricleListByTaglist")
    public Map getRecommendList(@RequestParam String myOpenid,
                                     @RequestParam List<String> tagList,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(获取相关标签的文章列表)");
                map.put("error", "无效的用户！！");
                return map;
            }

            List<Article> tempArticleList = articleService.findAllByTagLikeAndStatus(tagList,1);
            List<ArticleInfo> resultList = new ArrayList<>();

            if (tempArticleList == null||tempArticleList.isEmpty()) {
                logger.info("没有找到相关标签的文章（用户获取相关标签的文章）");
                map.put("message", "抱歉，没有找到相关标签的文章~");
                return map;
            }

            // 按创建时间倒序排序
            Collections.sort(tempArticleList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            //获取所求页数的专辑数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempArticleList.size(); i++) {
                resultList.add(new ArticleInfo(tempArticleList.get(i)));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<ArticleInfo> page = new PageImpl<>(resultList, pageable, tempArticleList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("用户获取相关标签的文章列表失败！！（后端发生某些错误）");
            map.put("error", "获取相关标签的文章列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }
}
