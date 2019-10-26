package com.gaocimi.flashpig.controller;
import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.HairstylistInfo;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.service.UserToHairstylistService;
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
 * @date 2019-10-16 19:37:30
 * @description
 */
@RestController
@ResponseResult
@Api(value = "用户服务", description = "用户操作相关业务")
public class UserController {
    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserToHairstylistService userToHairstylistService;



    @ApiOperation(value = "普通用户分页获取自己收藏的发型师列表")
    @GetMapping("/user/getMyHairstylists")
    public Map getMyCollectionByPage(String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize ) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            List<UserToHairstylist> tempRecordList= user.hairstylistRecordList;
            List<HairstylistInfo> resultList = new ArrayList<>();

            if(tempRecordList==null){
                logger.info("你还没有收藏的发型师哦~");
                map.put("message","你还没有收藏的发型师哦~");
                return map;
            }

            // 按时间倒序排序
            Collections.sort(tempRecordList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            //获取所求页数的发型师数据
            int first = pageNum*pageSize;
            int last = pageNum*pageSize+pageSize-1;
            for(int i = first ; i<=last&&i<tempRecordList.size() ; i++){
                HairstylistInfo info = new HairstylistInfo(tempRecordList.get(i));
                resultList.add(info);
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum,pageSize);
            Page<HairstylistInfo> page = new PageImpl<>(resultList, pageable, tempRecordList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己收藏的发型师列表失败！！（后端发生某些错误）");
            map.put("error", "获取收藏的发型师失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "收藏该发型师")
    @PostMapping("/user/addHairstylistToCollection")
    public Map addToCollection( String myOpenid,int hairstylistId){
        Map map = new HashMap();
        try{
            User user = userService.findUserByOpenid(myOpenid);
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if(user==null){
                logger.info("（"+myOpenid+"）该用户不存在！");
                map.put("error","无效的用户！！");
                return map;
            }
            if(hairstylist==null){
                logger.info("id为"+hairstylistId+"的发型师不存在！");
                map.put("error","该发型师不存在！！");
                return map;
            }
            if(userToHairstylistService.findByUserAndHairstylist(user.getId(),hairstylistId)!=null){
                logger.info("该用户已收藏该发型师，不需要重复收藏");
                map.put("message","已收藏该发型师!!");
                return map;
            }
            UserToHairstylist userToHairstylist = new UserToHairstylist(user,hairstylist);
            userToHairstylistService.save(userToHairstylist);
            logger.info("id为"+user.getId()+"的用户收藏了id为"+hairstylist.getId()+"的发型师“"+hairstylist.getHairstylistName()+"”");
            map.put("message","收藏成功！");

        }catch (Exception e){
            logger.info("后端发生异常：\n");
            logger.error(e.getMessage());
            map.put("error","抱歉，后端发生异常!!");
        }

        return map;
    }


//    @ApiOperation(value = "添加用户")
//    @PostMapping("/user")
//    @ResponseStatus(HttpStatus.CREATED)
//    public int addUser(@Validated User users) {
//        userService.save(users);
//        return 200;
//    }
//
//    @ApiOperation(value = "删除用户")
//    @DeleteMapping("/user/{userId}")
//    public int deleteUser( @PathVariable("userId") Integer userId) {
//        userService.delete(userId);
//        return 200;
//    }
//
//    @ApiOperation(value = "修改用户")
//    @PutMapping("/user")
//    public int updateUser(@Validated User users) {
//        userService.edit(users);
//        return 200;
//    }
//
//
//    @ApiOperation(value = "获取单个用户信息", produces = "application/json")
//    @GetMapping("/user/{userId}")
//    public User getOne(@PathVariable("userId") Integer userId) {
//        return userService.findUserById(userId);
//    }
//
//    @ApiOperation(value = "获取所有用户列表", produces = "application/json")
//    @GetMapping("/users")
//    public Page<User> getPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
//                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
//    ) {
//
//        Page<User> page = userService.findAll(pageNum,pageSize);
//        logger.info("hello");
//        Logger log = LogUtils.getPlatformLogger();
//        log.info("平台日志记录测试");
//
//        return page;
//    }

}
