//package com.gaocimi.flashpig.controller;
//
//import com.gaocimi.flashpig.entity.HairService;
//import com.gaocimi.flashpig.model.HairServiceExample;
//import com.gaocimi.flashpig.result.ResponseResult;
//import com.gaocimi.flashpig.service.HairServiceService;
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
//@Api(value = "管理端发型服务",description = "管理员操作发型服务相关业务")
//public class HairServiceController {
//    @Autowired
//    HairServiceService hairServiceService;
//
//    @ApiOperation(value = "添加发型服务",notes = "m1")
//    @PostMapping("/hairService")
//    @ResponseStatus(HttpStatus.CREATED)
//    public int addHairService(@Validated HairService hairServices) {
//        return hairServiceService.insertSelective(hairServices);
//    }
//
//    @ApiOperation(value = "删除发型服务",notes = "m1")
//    @DeleteMapping("/hairService/{hairServiceId}")
//    public int deleteHairService(@NotBlank @PathVariable("hairServiceId") Integer hairServiceId) {
//        return hairServiceService.deleteByPrimaryKey(hairServiceId);
//    }
//
//    @ApiOperation(value = "修改发型服务",notes = "m1")
//    @PutMapping("/hairService")
//    public int updateHairService(@Validated HairService hairServices) {
//        return hairServiceService.updateByPrimaryKeySelective(hairServices);
//    }
//
//
//    @ApiOperation(value = "获取单个发型服务信息",notes = "m1",produces = "application/json")
//    @GetMapping("/hairService/{hairServiceId}")
//    public HairService getOne( @PathVariable("hairServiceId") Integer hairServiceId) {
//        return hairServiceService.selectByPrimaryKey(hairServiceId);
//    }
//
//    @ApiOperation(value = "获取所有发型服务列表",notes = "m1",produces = "application/json")
//    @GetMapping("/hairServices")
//    public Page<HairService> getPage(@RequestParam(name="pageNum",defaultValue="0") int pageNum,
//                                 @RequestParam(name="pageSize",defaultValue="10") int pageSize,
//                                 @RequestParam(name="orderBy",defaultValue="id desc") String orderBy
//    ) {
//        Page<HairService> page = PageHelper.startPage(pageNum, pageSize, orderBy);
//        HairServiceExample example=new HairServiceExample();
//        hairServiceService.selectByExample(example);
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
