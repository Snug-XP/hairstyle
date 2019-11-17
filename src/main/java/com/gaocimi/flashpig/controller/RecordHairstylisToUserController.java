package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import com.gaocimi.flashpig.entity.RecordToUserImgUrl;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.model.NoteToOneUser;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.RecordHairstylisToUserService;
import com.gaocimi.flashpig.service.RecordToUserImgUrlService;
import com.gaocimi.flashpig.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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


    @ApiOperation(value = "发型师新增对顾客的备注 - 用于“发型师-预约列表-备注信息”页面的新增备注")
    @PostMapping("/hairstylist/addRecordToUser")
    public Map addRecordToUser(@RequestParam String myOpenid, @RequestParam Integer userId, @RequestParam String content,
                               @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            User user = userService.findUserById(userId);
            if ( hairstylist == null ) {
                logger.info("非发型师用户操作（发型师新增对顾客的备注）！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else if (user == null) {
                logger.info("备注对象（普通用户）不存在！！");
                map.put("error", "对不起，备注对象（普通用户）不存在！！");
                return map;
            } else {

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

                    recordToUserImgUrl.setRecordToUser(recordToUser);
                    recordToUserImgUrl.setImageUrl(image);

                    recordToUserImgUrlService.save(recordToUserImgUrl);
                }
                logger.info(hairstylist.getHairstylistName() + "（" + hairstylist.getOpenid() + "） 添加一条对" + user.getName() + "的备注！");
                map.put("message", "备注添加成功！");
                return map;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("备注添加操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取自己对某个顾客的备注记录(按时间倒序排序)-用于“发型师-预约列表-备注信息”页面")
    @GetMapping("/hairstylist/getNoteRecordToOneUser")
    public Map getNoteRecordToOneUser(@RequestParam String myOpenid, @RequestParam Integer userId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                List<RecordHairstylisToUser> tempRecordList = hairstylist.recordToUserList;
                List<RecordHairstylisToUser> resultRecordList = new ArrayList<>();

                //获取自己对某个顾客的备注类列表
                for (RecordHairstylisToUser record : tempRecordList) {
                    if (record.user.getId() == userId) {
                        resultRecordList.add(record);
                    }
                }

                //获取到自己对某个顾客的备注信息列表 即筛选出用于“发型师-预约列表-备注信息”页面的信息
                if (resultRecordList.size() > 0) {
                    List<NoteToOneUser> recordList = new ArrayList<>();
                    for (RecordHairstylisToUser resultRecord : resultRecordList) {
                        NoteToOneUser record = new NoteToOneUser();

                        record.setRecordId(resultRecord.getId());//设置对应备注的id
                        record.setCreateTime(resultRecord.getCreateTime());//设置备注的创建时间
                        record.setContent(resultRecord.getContent());//设置备注的内容

                        //取出对用户的备注类中的图片url列表
                        List<String> imgUrlList = new ArrayList<>();
                        for (RecordToUserImgUrl recordToUserImgUrl : resultRecord.getImageUrlList())
                            imgUrlList.add(recordToUserImgUrl.getImageUrl());
                        record.setImageUrlList(imgUrlList);

                        recordList.add(record);
                    }

                    // 按时间倒序排序
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r2.getCreateTime().after(r1.getCreateTime())) {
                            return 1;
                        } else if (r1.getCreateTime().after(r2.getCreateTime())) {
                            return -1;
                        }
                        return 0; //相等为0
                    });

                    map.put("recordList", recordList);
                } else
                    map.put("message", "没有匹配的记录");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己对某个顾客的备注信息列表失败！！（后端发生某些错误）");
            map.put("error", "获取备注列表操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据备注id，发型师删除对顾客的备注 - 用于“发型师-预约列表-备注信息”页面的删除备注")
    @DeleteMapping("/hairstylist/deleteRecordToUser")
    public Map deleteRecordToUser(@RequestParam String myOpenid, @RequestParam Integer recordId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }

            RecordHairstylisToUser recordToUser = recordHairstylisToUserService.findRecordHairstylisToUserById(recordId);
            if (recordToUser == null) {
                logger.info("备注不存在！！");
                map.put("error", "备注不存在！！");
                return map;
            }
            //判断该备注记录是不是该发型师用户的
            if (myOpenid.equals(recordToUser.getHairstylist().getOpenid())) {
                //执行删除操作

                recordHairstylisToUserService.delete(recordId);

                logger.info(hairstylist.getHairstylistName() + "(" + hairstylist.getOpenid() + ") 删除一条对" + recordToUser.getUser().getName() + "的备注！");
                map.put("message", "删除成功！");
                return map;
            } else {
                logger.info("没有删除备注的权限");
                map.put("error", "无删除权限！");
                return map;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误）");
            return map;
        }
    }


    @ApiOperation(value = "根据备注id，发型师修改对顾客的备注 - 用于“发型师-预约列表-备注信息”页面的修改备注")
    @PutMapping("/hairstylist/editRecordToUser")
    public Map editRecordToUser(@RequestParam String myOpenid, @RequestParam Integer recordId,@RequestParam String content,
                                @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }

            RecordHairstylisToUser recordToUser = recordHairstylisToUserService.findRecordHairstylisToUserById(recordId);
            if (recordToUser == null) {
                logger.info("所要修改的备注不存在！！");
                map.put("error", "所要修改的备注不存在！！");
                return map;
            }

            //判断该备注记录是不是该发型师用户的
            if (myOpenid.equals(recordToUser.getHairstylist().getOpenid())) {
                //执行修改操作

                recordToUser.setContent(content);
                recordHairstylisToUserService.edit(recordToUser);

                //保存备注对应的图片url
                //先删除该备注原来对应的所有图片url
                recordToUserImgUrlService.deleteAllByRecordId(recordId);
                for (String image : imageList) {
                    RecordToUserImgUrl recordToUserImgUrl = new RecordToUserImgUrl();

                    recordToUserImgUrl.setRecordToUser(recordToUser);
                    recordToUserImgUrl.setImageUrl(image);

                    recordToUserImgUrlService.save(recordToUserImgUrl);
                }

                logger.info(hairstylist.getHairstylistName() + "(" + hairstylist.getOpenid() + ") 修改了一条对" + recordToUser.getUser().getName() + "的备注！");
                map.put("message", "修改成功！");
                return map;
            } else {
                logger.info("没有删除备注的权限");
                map.put("error", "无删除权限！");
                return map;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误）");
            return map;
        }
    }


}
