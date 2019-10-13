package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import com.gaocimi.flashpig.entity.RecordToUserImgUrl;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.RecordHairstylisToUserService;
import com.gaocimi.flashpig.service.RecordToUserImgUrlService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.utils.CustomDatePropertyEditor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liyutg
 * @date 2019/6/12 2:15
 * @description
 */
@RestController
@ResponseResult
@Api(value = "发型师对顾客的备注服务", description = "备注操作相关业务")
public class RecordHairstylisToUserController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    RecordHairstylisToUserService recordHairstylisToUserService;
    @Autowired
    RecordToUserImgUrlService recordToUserImgUrlService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserService userService;


    @ApiOperation(value = "发型师新增对顾客的备注 - 用于“发型师-预约列表-备注信息”页面的新增备注", notes = "m1")
    @PostMapping("/hairstylist/addRecordToUser")
    public Map addRecordToUser(String myOpenid, int userId, String content,
                               @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else if (userService.findUserById(userId) == null) {
                logger.info("备注对象（普通用户）不存在！！");
                map.put("error", "对不起，备注对象（普通用户）不存在！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                User user = userService.findUserById(userId);
                RecordHairstylisToUser recordToUser = new RecordHairstylisToUser();

                recordToUser.setHairstylist(hairstylist);
                recordToUser.setUser(user);
                recordToUser.setContent(content);

                //设置创建时间
                Date date = new Date(System.currentTimeMillis());
                recordToUser.setCreateTime(date);

                recordHairstylisToUserService.save(recordToUser);//保存后会给recordToUser自动分配主键值

                //保存备注对应的图片url
                for (String image : imageList) {
                    RecordToUserImgUrl recordToUserImgUrl = new RecordToUserImgUrl();
                    recordToUserImgUrl.setImageUrl(image);
                    recordToUserImgUrl.setRecordToUser(recordToUser);

                    recordToUserImgUrlService.save(recordToUserImgUrl);
                }
                logger.info("备注添加成功！");
                map.put("message","备注添加成功！");
                return map;
            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }


    @ApiOperation(value = "发型师删除对顾客的备注 - 用于“发型师-预约列表-备注信息”页面的删除备注", notes = "m1")
    @DeleteMapping("/hairstylist/deleteRecordToUser")
    public Map deleteRecordToUser( String myOpenid, int userId, String content,
                               @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else if (userService.findUserById(userId) == null) {
                logger.info("备注对象（普通用户）不存在！！");
                map.put("error", "对不起，备注对象（普通用户）不存在！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                User user = userService.findUserById(userId);
                RecordHairstylisToUser recordToUser = new RecordHairstylisToUser();

                recordToUser.setHairstylist(hairstylist);
                recordToUser.setUser(user);
                recordToUser.setContent(content);

                //设置创建时间
                Date date = new Date(System.currentTimeMillis());
                recordToUser.setCreateTime(date);

                recordHairstylisToUserService.save(recordToUser);//保存后会给recordToUser自动分配主键值

                //保存备注对应的图片url
                for (String image : imageList) {
                    RecordToUserImgUrl recordToUserImgUrl = new RecordToUserImgUrl();
                    recordToUserImgUrl.setImageUrl(image);
                    recordToUserImgUrl.setRecordToUser(recordToUser);

                    recordToUserImgUrlService.save(recordToUserImgUrl);
                }
                logger.info("备注添加成功！");
                map.put("message","备注添加成功！");
                return map;
            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }


//    @ApiOperation(value = "删除备注", notes = "m1")
//    @DeleteMapping("/recordHairstylisToUser/{recordHairstylisToUserId}")
//    public int deleteRecordHairstylisToUser( @PathVariable("recordHairstylisToUserId") Integer
//                                                    recordHairstylisToUserId) {
//        try {
//            recordHairstylisToUserService.delete(recordHairstylisToUserId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 500;
//        }
//        return 200;
//    }
//
//    @ApiOperation(value = "修改备注", notes = "m1")
//    @PutMapping("/recordHairstylisToUser")
//    public int updateRecordHairstylisToUser(@Validated RecordHairstylisToUser recordHairstylisToUsers) {
//        try {
//            recordHairstylisToUserService.edit(recordHairstylisToUsers);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 500;
//        }
//        return 200;
//    }
//
//
//    @ApiOperation(value = "获取单个备注信息", notes = "m1", produces = "application/json")
//    @GetMapping("/recordHairstylisToUser/{recordHairstylisToUserId}")
//    public RecordHairstylisToUser getOne(@PathVariable("recordHairstylisToUserId") Integer recordHairstylisToUserId) {
//        return recordHairstylisToUserService.findRecordHairstylisToUserById(recordHairstylisToUserId);
//    }

//    @ApiOperation(value = "获取所有备注列表", notes = "m1", produces = "application/json")
//    @GetMapping("/recordHairstylisToUsers")
//    public Page<RecordHairstylisToUser> getPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
//                                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
//                                                @RequestParam(name = "orderBy", defaultValue = "id desc") String orderBy
//    ) {
//        Page<RecordHairstylisToUser> page = recordHairstylisToUserService.findAll(pageNum, pageSize);
//        return page;
//    }


}
