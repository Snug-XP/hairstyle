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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AdministratorService administratorService;
    @Autowired
    ShopService shopService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    ArticleService articleService;
    @Autowired
    PushSubscribeMessageController pushWxMsg;
    @Autowired
    UserService userService;

    @ApiOperation(value = "获取管理端页面初始化所需数据")
    @GetMapping("/Administrator/getData")
    public Map getData(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            if (!administratorService.isExist(myOpenid)) {
                map.put("error", "无权限！");
                return map;
            }

            map.put("userNum", userService.getCount());
            map.put("verifiedShopNum", shopService.countAllByStatus(1));
            map.put("verifiedHairstylistNum", hairstylistService.countAllByStatus(1));
            map.put("pendingReviewNum", shopService.countAllByStatus(0));

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取管理端页面初始化所需数据失败！！（后端发生某些错误）");
            map.put("error", "获取数据失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取待审核或审核已通过的门店信息列表(分页展示)（status=0表示“待审核”status=1表示“审核通过”，status=-1表示“审核未通过”，可选定省、市、县以及门店名的范围）", notes = "仅管理员有权限")
    @GetMapping("/Administrator/getRegisterShopList")
    public Map getRegisterShopList(@RequestParam String myOpenid,
                                   @RequestParam Integer status,
                                   @RequestParam(name = "province", required = false) String province,
                                   @RequestParam(name = "city", required = false) String city,
                                   @RequestParam(name = "district", required = false) String district,
                                   @RequestParam(name = "shopName", required = false) String shopName,
                                   @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(myOpenid)) {
                Page<Shop> page = shopService.findRegisterShopList(status, province, city, district, shopName, pageNum, pageSize);
                map.put("page", page);
                return map;
            } else {
                logger.info("获取正在注册的门店信息失败！！（没有权限！！）");
                map.put("error", "获取正在注册的门店信息失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取正在注册的门店列表信息失败！！（后端发生某些错误）");
            map.put("error", "获取正在注册的门店列表信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "同意或拒绝门店的认证（decide=1表示同意，decide=-1表示不同意）", notes = "仅管理员有权限", produces = "application/json")
    @PostMapping("/Administrator/shop/approveOrReject")
    public Map approveOrReject(@RequestParam String myOpenid, int shopId, int decide) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if (administrator != null) {

                Shop shop = shopService.findShopById(shopId);
                if (shop == null) {
                    map.put("error", "所操作的门店不存在！！");
                    logger.info("所操作的门店不存在！！");
                    return map;
                }

                switch (decide) {
                    case 1:
                        shop.setApplyStatus(1);
                        shop.setApplyTime(new Date(System.currentTimeMillis()));
                        shopService.edit(shop);
                        logger.info("管理员“" + administrator.getName() + "”(id=" + administrator.getId() + ")同意了id为" + shop.getId() + "的发型师“" + shop.getShopName() + "”的认证");
                        map.put("message", "同意该门店认证，操作成功");
                        pushWxMsg.pushApplyResultMessage2(shopId);
                        break;
                    case -1:
                        shop.setApplyStatus(-1);
                        shopService.edit(shop);
                        logger.info("管理员“" + administrator.getName() + "”(id=" + administrator.getId() + ")拒绝了id为" + shop.getId() + "的门店“" + shop.getShopName() + "”的认证！");
                        map.put("message", "拒绝该门店认证，操作成功");
                        //...有时间再加一下拒绝理由模版消息
                        pushWxMsg.pushApplyResultMessage2(shopId);
                        break;
                    default:
                        map.put("error", "decide的值错误（同意为1，拒绝为-1）！！");
                        logger.info("同意或拒绝门店认证的decide(=" + decide + ")的值错误！！");
                        break;

                }
            } else {
                logger.info("同意或拒绝门店认证操作失败！！（没有权限！！）");
                map.put("error", "操作失败！！（没有权限！！）");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("同意或拒绝门店认证操作失败！！（后端发生某些错误）");
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
                Page<Article> page = articleService.findAllByStatus(0, pageNum, pageSize);
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
