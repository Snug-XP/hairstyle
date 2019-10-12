//package com.gaocimi.flashpig.controller;
//
//import com.gaocimi.flashpig.entity.User;
//import com.gaocimi.flashpig.result.ResponseResult;
//import com.gaocimi.flashpig.service.UserService;
//import com.gaocimi.flashpig.utils.CustomDatePropertyEditor;
//import com.gaocimi.flashpig.utils.LogUtils;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.hibernate.validator.constraints.NotBlank;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.context.request.WebRequest;
//
//import java.beans.PropertyEditor;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author liyutg
// * @date 2019/6/12 2:15
// * @description
// */
//@RestController
//@ResponseResult
//@Api(value = "管理端用户服务", description = "管理员操作用户相关业务")
//public class UserController {
//    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);
//
//    @Autowired
//    UserService userService;
//
//    @ApiOperation(value = "添加用户", notes = "m1")
//    @PostMapping("/user")
//    @ResponseStatus(HttpStatus.CREATED)
//    public int addUser(@Validated User users) {
//        userService.save(users);
//        return 200;
//    }
//
//    @ApiOperation(value = "删除用户", notes = "m1")
//    @DeleteMapping("/user/{userId}")
//    public int deleteUser(@NotBlank @PathVariable("userId") Integer userId) {
//        userService.delete(userId);
//        return 200;
//    }
//
//    @ApiOperation(value = "修改用户", notes = "m1")
//    @PutMapping("/user")
//    public int updateUser(@Validated User users) {
//        userService.edit(users);
//        return 200;
//    }
//
//
//    @ApiOperation(value = "获取单个用户信息", notes = "m1", produces = "application/json")
//    @GetMapping("/user/{userId}")
//    public User getOne(@PathVariable("userId") Integer userId) {
//        return userService.findUserById(userId);
//    }
//
//    @ApiOperation(value = "获取所有用户列表", notes = "m1", produces = "application/json")
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
//
//
//    @InitBinder
//    public void initBinder(WebDataBinder binder, WebRequest request) {
//        //转换日期
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        dateFormat.setLenient(false);
//        // CustomDatePropertyEditor 为自定义日期编辑器
//        PropertyEditor dateEditor = new CustomDatePropertyEditor(dateFormat, true, null);
//        binder.registerCustomEditor(Date.class, dateEditor);
//    }
//}
