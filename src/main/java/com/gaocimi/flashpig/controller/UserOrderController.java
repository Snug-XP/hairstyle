//package com.gaocimi.flashpig.controller;
//
//import com.gaocimi.flashpig.entity.UserOrder;
//import com.gaocimi.flashpig.model.UserOrderExample;
//import com.gaocimi.flashpig.result.ResponseResult;
//import com.gaocimi.flashpig.service.UserOrderService;
//import com.gaocimi.flashpig.utils.CustomDatePropertyEditor;
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.hibernate.validator.constraints.NotBlank;
//import org.springframework.beans.factory.annotation.Autowired;
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
///**
// * @author liyutg
// * @date 2019/6/12 2:15
// * @description
// */
//@RestController
//@ResponseResult
//@Api(value = "管理端用户订单服务",description = "管理员操作用户订单相关业务")
//public class UserOrderController {
//    @Autowired
//    UserOrderService userOrderService;
//
//    @ApiOperation(value = "添加用户订单",notes = "m1")
//    @PostMapping("/userOrder")
//    @ResponseStatus(HttpStatus.CREATED)
//    public int addUserOrder(@Validated UserOrder userOrders) {
//        return userOrderService.insertSelective(userOrders);
//    }
//
//    @ApiOperation(value = "删除用户订单",notes = "m1")
//    @DeleteMapping("/userOrder/{userOrderId}")
//    public int deleteUserOrder(@NotBlank @PathVariable("userOrderId") Integer userOrderId) {
//        return userOrderService.deleteByPrimaryKey(userOrderId);
//    }
//
//    @ApiOperation(value = "修改用户订单",notes = "m1")
//    @PutMapping("/userOrder")
//    public int updateUserOrder(@Validated UserOrder userOrders) {
//        return userOrderService.updateByPrimaryKeySelective(userOrders);
//    }
//
//
//    @ApiOperation(value = "获取单个用户订单信息",notes = "m1",produces = "application/json")
//    @GetMapping("/userOrder/{userOrderId}")
//    public UserOrder getOne( @PathVariable("userOrderId") Integer userOrderId) {
//        return userOrderService.selectByPrimaryKey(userOrderId);
//    }
//
//    @ApiOperation(value = "获取所有用户订单列表",notes = "m1",produces = "application/json")
//    @GetMapping("/userOrders")
//    public Page<UserOrder> getPage(@RequestParam(name="pageNum",defaultValue="0") int pageNum,
//                                 @RequestParam(name="pageSize",defaultValue="10") int pageSize,
//                                 @RequestParam(name="orderBy",defaultValue="id desc") String orderBy
//    ) {
//        Page<UserOrder> page = PageHelper.startPage(pageNum, pageSize, orderBy);
//        UserOrderExample example=new UserOrderExample();
//        userOrderService.selectByExample(example);
//        return page;
//    }
//
//
//    @InitBinder
//    public void initBinder(WebDataBinder binder, WebRequest request) {
//        //转换日期
//        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        dateFormat.setLenient(false);
//        // CustomDatePropertyEditor 为自定义日期编辑器
//        PropertyEditor dateEditor = new CustomDatePropertyEditor(dateFormat , true , null);
//        binder.registerCustomEditor(Date.class, dateEditor);
//    }
//}
