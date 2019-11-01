package com.gaocimi.flashpig.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import com.gaocimi.flashpig.entity.Administrator;
import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.AdministratorService;
import com.gaocimi.flashpig.service.ArticleService;
import com.gaocimi.flashpig.service.HairstylistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @author xp
 * @Date 2019-10-12 15:44:00
 * @description 系统管理员操作
 */
@RestController
@ResponseResult
@Api(value = "管理端操作", description = "")
class AdministratorController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    AdministratorService administratorService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    ArticleService articleService;

    @ApiOperation(value = "获取正在注册的发型师信息列表(分页展示)", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/Administrator/hairstylist/getRegisterList")
    public Map getRegisterList(@RequestParam String myOpenid,
                               @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(myOpenid)) {
                Page<Hairstylist> page = hairstylistService.findRegisterList(pageNum, pageSize);
                map.put("page", page);
                logger.info("获取发型师列表信息成功！");
                return map;
            } else {
                logger.info("获取正在注册的发型师信息失败！！（没有权限！！）");
                map.put("error", "获取正在注册的发型师信息失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取正在注册的发型师列表信息失败！！（后端发生某些错误）");
            map.put("error", "获取正在注册的发型师列表信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "同意或拒绝发型师的注册（decide=1表示同意，decide=-1表示不同意）", notes = "仅管理员有权限", produces = "application/json")
    @PostMapping("/Administrator/hairstylist/approveOrReject")
    public Map approveOrReject(@RequestParam String myOpenid, int hairstylistId, int decide) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if (administrator != null) {

                Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
                if (hairstylist == null) {
                    map.put("error", "所操作的发型师不存在！！");
                    logger.info("所操作的发型师不存在！！");
                    return map;
                }


                switch (decide) {
                    case 1:
                        hairstylist.setApplyStatus(1);
                        hairstylistService.edit(hairstylist);
                        logger.info("管理员“" + administrator.getName() + "”(id=" + administrator.getId() + ")同意id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”的注册");
                        map.put("message", "同意该发型师注册，操作成功");
                        break;
                    case -1:
                        hairstylist.setApplyStatus(-1);
                        hairstylistService.edit(hairstylist);
                        logger.info("管理员“" + administrator.getName() + "”(id=" + administrator.getId() + ")拒绝id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”的注册！");
                        map.put("message", "拒绝该发型师注册，操作成功");
                        //...有时间再加一下拒绝理由模版消息
                        break;
                    default:
                        map.put("error", "decide的值错误（同意为1，拒绝为-1）！！");
                        logger.info("同意或拒绝发型师注册的decide的值错误！！");
                        break;

                }
            } else {
                logger.info("同意或拒绝发型师注册操作息失败！！（没有权限！！）");
                map.put("error", "操作失败！！（没有权限！！）");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("同意或拒绝发型师注册操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();

        }
        return map;
    }


    @ApiOperation(value = "获取待审核的文章列表(分页展示)", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/Administrator/article/getPendingList")
    public Map getairstylistsPage(@RequestParam String myOpenid,
                                  @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(myOpenid)) {
                Page<Article> page = articleService.findPendingList(pageNum, pageSize);
                map.put("page", page);
                logger.info("获取待审核的文章列表信息成功！");
                return map;
            } else {
                logger.info("获取待审核的文章列表失败！！（没有权限！！）");
                map.put("error", "获取待审核的文章列表失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取待审核的文章列表失败！！（后端发生某些错误）");
            map.put("error", "获取待审核的文章列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "同意或拒绝发型文章的发表（decide=1表示同意，decide=-1表示不同意）", notes = "仅管理员有权限", produces = "application/json")
    @PostMapping("/Administrator/article/approveOrReject")
    public Map approveOrRejectArticle(@RequestParam String myOpenid, @RequestParam Integer articleId, @RequestParam Integer decide) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if (administrator != null) {

                Article article = articleService.findArticleById(articleId);
                if (article == null) {
                    map.put("error", "所操作的发型文章不存在！！");
                    logger.info("发型文章不存在！！");
                    return map;
                }

                switch (decide) {
                    case 1:
                        article.setStatus(1);
                        articleService.edit(article);
                        logger.info("管理员“" + administrator.getName() + "”(id=" + administrator.getId() + ")同意发型id为" + article.getId() + "的文章“" + article.getTitle() + "”的发表操作成功！");
                        map.put("message", "同意发表，操作成功");
                        break;
                    case -1:
                        article.setStatus(-1);
                        articleService.edit(article);
                        logger.info("管理员“" + administrator.getName() + "”(id=" + administrator.getId() + ")拒绝发型id为" + article.getId() + "的文章“" + article.getTitle() + "”的发表操作成功！");
                        map.put("message", "拒绝发表，操作成功");
                        //...有时间再加一下拒绝理由模版消息
                        break;
                    default:
                        map.put("error", "decide的值错误（同意为1，拒绝为-1）！！");
                        logger.info("同意或拒绝发型文章发表的decide的值错误！！");
                        break;

                }
            } else {
                logger.info("同意或拒绝发型文章发表操作失败！！（没有权限！！）");
                map.put("error", "操作失败！！（没有权限！！）");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("同意或拒绝发型文章发表操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();

        }
        return map;
    }

}
