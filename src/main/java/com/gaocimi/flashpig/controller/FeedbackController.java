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
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author xp
 * @date 2019-12-31 17:12:04
 * @description 用户的反馈相关业务
 */
@RestController
@ResponseResult
@Api(value = "用户的反馈服务相关业务", description = "用户的反馈相关业务")
public class FeedbackController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    FeedbackService feedbackService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserService userService;
    @Autowired
    AdministratorService administratorService;
    @Autowired
    AlbumService albumService;

    @ApiOperation(value = "新增反馈")
    @PostMapping("/addFeedback")
    public Map addFeedback(@RequestParam String myOpenid, @RequestParam String content) {
        Map map = new HashMap();
        try {

            Feedback feedback = new Feedback();
            feedback.setOpenid(myOpenid);
            feedback.setContent(content);
            feedback.setCreateTime(new Date(System.currentTimeMillis()));

            feedbackService.save(feedback);

            map.put("message", "反馈上传成功！");
            return map;
        } catch (Exception e) {
            logger.info("用户的反馈上传失败！！（后端发生某些错误）");
            map.put("error", "反馈上传失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "删除反馈", notes = "权限：仅用户的反馈的发布者或管理员")
    @DeleteMapping("/feedback")
    public Map deleteFeedback(@RequestParam String myOpenid, @RequestParam Integer feedbackId) {

        Map map = new HashMap();
        try {
            Feedback feedback = feedbackService.findFeedbackById(feedbackId);
            if (feedback == null) {
                logger.info("id为" + feedbackId + "的反馈不存在（删除用户的反馈）！");
                map.put("error", "该反馈不存在！！");
                return map;
            }

            logger.info("openid为" + myOpenid + "的用户删除了id为" + feedbackId + "的用户的反馈:" + feedback.getContent());
            feedbackService.delete(feedbackId);
            map.put("message", "删除成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("用户的反馈删除失败！！（后端发生某些错误）");
            map.put("error", "用户的反馈删除失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "修改反馈", notes = "权限：仅用户的反馈的发布者")
    @PutMapping("/hairstylist/updateFeedback")
    public Map updateFeedback(@RequestParam String myOpenid, @RequestParam Integer feedbackId, @RequestParam String content) {
        Map map = new HashMap();
        try {

            Feedback feedback = feedbackService.findFeedbackById(feedbackId);

            if (feedback == null) {
                logger.info("id为" + feedbackId + "的用户的反馈不存在（修改用户的反馈）！");
                map.put("error", "该用户的反馈不存在！！");
                return map;
            }

            feedback.setContent(content);
            feedback.setCreateTime(new Date(System.currentTimeMillis()));

            feedbackService.edit(feedback);

            logger.info("openid为" + myOpenid + "的用户修改了id为" + feedbackId + "的用户的反馈位:" + feedback.getContent());

            map.put("message", "用户的反馈修改成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("用户的反馈修改失败！！（后端发生某些错误）");
            map.put("error", "用户的反馈修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取单个用户的反馈信息", produces = "application/json")
    @GetMapping("/feedback/getOne")
    public Map getOne(@RequestParam Integer feedbackId) {
        Map map = new HashMap();
        try {
            Feedback feedback = feedbackService.findFeedbackById(feedbackId);

            map.put("feedback", feedback);
            return map;

        } catch (Exception e) {
            logger.info("后端发生异常：\n");
            map.put("error", "抱歉，后端发生异常!!");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取所有用户的反馈列表（分页展示）")
    @GetMapping("/feedbacks/getAll")
    public Map getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {

        Map map = new HashMap();
        try {
            Page<Feedback> page = feedbackService.findAll(pageNum, pageSize);
            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.info("后端发生异常：\n");
            map.put("error", "抱歉，后端发生异常!!");
            e.printStackTrace();
            return map;
        }
    }
}

