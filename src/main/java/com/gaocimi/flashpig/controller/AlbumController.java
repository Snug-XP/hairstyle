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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.util.*;

/**
 * @author xp
 * @date 2019-10-24 22:29:08
 * @description 专辑相关业务
 */
@RestController
@ResponseResult
@Api(value = "专辑操作的相关业务", description = "专辑相关业务")
public class AlbumController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    AlbumService albumService;
    @Autowired
    ArticleService articleService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserService userService;
    @Autowired
    AdministratorService administratorService;
    @Autowired
    OssAccessController ossAccessController;

    @ApiOperation(value = "管理员创建专辑")
    @PostMapping("/administrator/addAlbum")
    public Map addAlbum(@RequestParam String myOpenid, @RequestParam String title, @RequestParam String imgUrl,
                        @RequestParam(value = "tagList", required = false) List<String> tagList) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }

            Album album = new Album();

            album.setAdministrator(administrator);
            album.setTitle(title);
            album.setTag(tagList);
            album.setImgUrl(imgUrl);
            album.setCreateTime(new Date(System.currentTimeMillis()));
            albumService.save(album);

            logger.info("id为" + administrator.getId() + "的管理员“" + administrator.getName() + "”上传了id为" + album.getId() + "的专辑“" + album.getTitle() + "”");
            map.put("message", "专辑上传成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("专辑上传失败！！（后端发生某些错误）");
            map.put("error", "专辑上传失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "管理员修改专辑")
    @PutMapping("/administrator/updateAlbum")
    public Map updateAlbum(@RequestParam String myOpenid, @RequestParam Integer albumId, @RequestParam String title, @RequestParam String imgUrl,
                           @RequestParam(value = "tagList", required = false) List<String> tagList) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }

            Album album = albumService.findAlbumById(albumId);
            if (album == null) {
                logger.info("id为" + albumId + "的专辑不存在！");
                map.put("error", "id为" + albumId + "的专辑不存在！");
                return map;
            }

            album.setTitle(title);
            album.setTag(tagList);
            album.setImgUrl(imgUrl);
            albumService.edit(album);

            logger.info("id为" + administrator.getId() + "的管理员“" + administrator.getName() + "”修改了id为" + albumId + "的专辑");
            map.put("message", "专辑修改成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("专辑修改失败！！（后端发生某些错误）");
            map.put("error", "专辑修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据专辑id，删除专辑")
    @DeleteMapping("/album")
    public Map deleteAlbum(@RequestParam String myOpenid, @RequestParam Integer albumId) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }
            Album album = albumService.findAlbumById(albumId);

            if (album == null) {
                logger.info("id为" + albumId + "的专辑不存在！");
                map.put("error", "该专辑不存在！");
                return map;
            }
            map = ossAccessController.deleteObject(myOpenid, album.getImgUrl());
            logger.info("oss操作结果：" + map + "\n");
            map.clear();

            albumService.delete(albumId);
            logger.info("id为" + administrator.getId() + "的管理员“" + administrator.getName() + "”删除了id为" + albumId + "的专辑“" + album.getTitle() + "”");
            map.put("message", "删除成功!");
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("删除专辑失败！！（后端发生某些错误）");
            map.put("error", "删除专辑失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
        return map;
    }


    @ApiOperation(value = "根据专辑id，获取与该专辑标签相关的所有文章列表（分页展示）")
    @GetMapping("/album/getArticleByTagList")
    public Map getAllByTagList(@RequestParam Integer albumId,
                               @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Album album = albumService.findAlbumById(albumId);
            if (album == null) {
                logger.info("id为" + albumId + "的专辑不存在！");
                map.put("error", "id为" + albumId + "的专辑不存在！");
                return map;
            }
            List<Article> tempList = articleService.findAllByTagLike(album.getTag());
            List<ArticleInfo> resultList = new ArrayList<>();

            if (tempList.size() == 0) {
                map.put("message", "未找到相关标签的文章");
                return map;
            }

            // 按标题升序排序
            Collections.sort(tempList, (o1, o2) -> Collator.getInstance(Locale.CHINESE).compare(o1.getTitle(), o2.getTitle()));

            //获取所求页数的专辑数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempList.size(); i++) {
                resultList.add(new ArticleInfo(tempList.get(i)));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<ArticleInfo> page = new PageImpl<>(resultList, pageable, tempList.size());

            map.put("page", page);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取与该专辑标签相关的所有文章列表失败！！（后端发生某些错误）");
            map.put("error", "获取相关文章列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

        return map;
    }

    @ApiOperation(value = "根据专辑id，获取单个专辑中的文章列表以及该专辑的相关信息（分页展示）", produces = "application/json")
    @GetMapping("/album/articleList")
    public Map getArticleList(@RequestParam Integer albumId,
                              @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Album album = albumService.findAlbumById(albumId);
            if (album == null) {
                logger.info("id为" + albumId + "的专辑不存在！");
                map.put("error", "id为" + albumId + "的专辑不存在！");
                return map;
            }
            List<Article> tempList = album.getArticleList();
            List<ArticleInfo> resultList = new ArrayList<>();

            // 按标题升序排序
            Collections.sort(tempList, (o1, o2) -> Collator.getInstance(Locale.CHINESE).compare(o1.getTitle(), o2.getTitle()));

            //获取所求页数的专辑数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempList.size(); i++) {
                resultList.add(new ArticleInfo(tempList.get(i)));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<ArticleInfo> page = new PageImpl<>(resultList, pageable, tempList.size());


            map.put("albumInfo", album);
            map.put("page", page);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取与该专辑中的文章列表失败！！（后端发生某些错误）");
            map.put("error", "获取相关文章列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
        return map;
    }

    @ApiOperation(value = "根据专辑id，获取单个专辑中的相关信息", produces = "application/json")
    @GetMapping("/album")
    public Map getOne(@RequestParam Integer albumId) {
        Map map = new HashMap();
        Album album = albumService.findAlbumById(albumId);
        if (album == null) {
            logger.info("id为" + albumId + "的专辑不存在！");
            map.put("error", "id为" + albumId + "的专辑不存在！");
            return map;
        }
        map.put("album", album);
        return map;
    }

    @ApiOperation(value = "获取所有专辑列表")
    @GetMapping("/albums/getAll")
    public Page<Album> getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<Album> page = albumService.findAll(pageNum, pageSize);
        return page;
    }


    @ApiOperation(value = "管理员分页获取自己创建的专辑列表")
    @GetMapping("/album/getMyAlbum")
    public Map getMyCollectionByPage(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }

            List<Album> tempAlbumList = administrator.getAlbumList();
            List<Album> resultAlbumList = new ArrayList<>();

            if (tempAlbumList == null) {
                logger.info("你还没有创建的专辑哦~");
                map.put("message", "你还没有创建的专辑哦~");
                return map;
            }

            // 按时间倒序排序
            Collections.sort(tempAlbumList, (o1, o2) -> {
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
            for (int i = first; i <= last && i < tempAlbumList.size(); i++) {
                resultAlbumList.add(tempAlbumList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Album> page = new PageImpl<>(resultAlbumList, pageable, tempAlbumList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己创建的专辑列表失败！！（后端发生某些错误）");
            map.put("error", "获取自己创建的专辑列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "将选择的文章列表加入某专辑", notes = "权限：仅管理员")
    @PostMapping("/album/addArticleListToAlbum")
    public Map addArticleListToAlbum(@RequestParam String myOpenid, @RequestParam List<Integer> articleIdList, @RequestParam Integer albumId) {
        Map map = new HashMap();
        List<Integer> idList = new ArrayList<>();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作（addArticleListToAlbum）！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }
            Album album = albumService.findAlbumById(albumId);
            if (album == null) {
                logger.info("id为" + albumId + "的专辑不存在！（addArticleListToAlbum）");
                map.put("error", "id为" + albumId + "的专辑不存在！！");
                return map;
            }

            for (Integer articleId : articleIdList) {
                Article article = articleService.findArticleById(articleId);
                if (article == null) {
                    logger.info("id为" + articleId + "的文章不存在！（addArticleListToAlbum）");
                    continue;
                }
                //...这边可以使用集合来优化
                if (album.existArticle(articleId)) {
                    logger.info("该专辑(id=" + albumId + ")已收录该发型文章(id=" + articleId + ")，不需要重复收录");
                    continue;
                }
                idList.add(articleId);
                album.articleList.add(article);
            }
            albumService.edit(album);

            logger.info("id为" + albumId + "的专辑（title：" + album.getTitle() + "）新收录了" + idList.size() + "个发型文章（idList：" + idList + "）");
            map.put("message", "收录成功!( + " + idList.size() + ")");

        } catch (
                Exception e) {
            logger.info("后端发生异常\n");
            map.put("error", "抱歉，后端发生异常!!");
            e.printStackTrace();
        }

        return map;
    }
}
