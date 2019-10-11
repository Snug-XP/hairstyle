//package com.gaocimi.flashpig.controller;
//
//import com.gaocimi.flashpig.entity.HaircutOrder;
//import com.gaocimi.flashpig.model.HaircutOrderExample;
//import com.gaocimi.flashpig.result.ResponseResult;
//import com.gaocimi.flashpig.service.HaircutOrderService;
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
//public class HaircutOrderController {
//    @Autowired
//    HaircutOrderService haircutOrderService;
//
//    @ApiOperation(value = "添加用户订单",notes = "m1")
//    @PostMapping("/haircutOrder")
//    @ResponseStatus(HttpStatus.CREATED)
//    public int addHaircutOrder(@Validated HaircutOrder haircutOrders) {
//        return haircutOrderService.insertSelective(haircutOrders);
//    }
//
//    @ApiOperation(value = "删除用户订单",notes = "m1")
//    @DeleteMapping("/haircutOrder/{haircutOrderId}")
//    public int deleteHaircutOrder(@NotBlank @PathVariable("haircutOrderId") Integer haircutOrderId) {
//        return haircutOrderService.deleteByPrimaryKey(haircutOrderId);
//    }
//
//    @ApiOperation(value = "修改用户订单",notes = "m1")
//    @PutMapping("/haircutOrder")
//    public int updateHaircutOrder(@Validated HaircutOrder haircutOrders) {
//        return haircutOrderService.updateByPrimaryKeySelective(haircutOrders);
//    }
//
//
//    @ApiOperation(value = "获取单个用户订单信息",notes = "m1",produces = "application/json")
//    @GetMapping("/haircutOrder/{haircutOrderId}")
//    public HaircutOrder getOne( @PathVariable("haircutOrderId") Integer haircutOrderId) {
//        return haircutOrderService.selectByPrimaryKey(haircutOrderId);
//    }
//
//    @ApiOperation(value = "获取所有用户订单列表",notes = "m1",produces = "application/json")
//    @GetMapping("/haircutOrders")
//    public Page<HaircutOrder> getPage(@RequestParam(name="pageNum",defaultValue="0") int pageNum,
//                                 @RequestParam(name="pageSize",defaultValue="10") int pageSize,
//                                 @RequestParam(name="orderBy",defaultValue="id desc") String orderBy
//    ) {
//        Page<HaircutOrder> page = PageHelper.startPage(pageNum, pageSize, orderBy);
//        HaircutOrderExample example=new HaircutOrderExample();
//        haircutOrderService.selectByExample(example);
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
