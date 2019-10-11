package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.RecordHairstylisToUserService;
import com.gaocimi.flashpig.utils.CustomDatePropertyEditor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.NotBlank;
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
import java.util.Date;

/**
 * @author liyutg
 * @date 2019/6/12 2:15
 * @description
 */
@RestController
@ResponseResult
@Api(value = "管理端备注服务", description = "管理员操作备注相关业务")
public class RecordHairstylisToUserController {
    @Autowired
    RecordHairstylisToUserService recordHairstylisToUserService;

    @ApiOperation(value = "添加备注", notes = "m1")
    @PostMapping("/recordHairstylisToUser")
    @ResponseStatus(HttpStatus.CREATED)
    public int addRecordHairstylisToUser(@Validated RecordHairstylisToUser recordHairstylisToUsers) {
        try {
            recordHairstylisToUserService.save(recordHairstylisToUsers);
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return 200;
    }

    @ApiOperation(value = "删除备注", notes = "m1")
    @DeleteMapping("/recordHairstylisToUser/{recordHairstylisToUserId}")
    public int deleteRecordHairstylisToUser(@NotBlank @PathVariable("recordHairstylisToUserId") Integer recordHairstylisToUserId) {
        try {
            recordHairstylisToUserService.delete(recordHairstylisToUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return 200;
    }

    @ApiOperation(value = "修改备注", notes = "m1")
    @PutMapping("/recordHairstylisToUser")
    public int updateRecordHairstylisToUser(@Validated RecordHairstylisToUser recordHairstylisToUsers) {
        try {
            recordHairstylisToUserService.edit(recordHairstylisToUsers);
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return 200;
    }


    @ApiOperation(value = "获取单个备注信息", notes = "m1", produces = "application/json")
    @GetMapping("/recordHairstylisToUser/{recordHairstylisToUserId}")
    public RecordHairstylisToUser getOne(@PathVariable("recordHairstylisToUserId") Integer recordHairstylisToUserId) {
        return recordHairstylisToUserService.findRecordHairstylisToUserById(recordHairstylisToUserId);
    }

    @ApiOperation(value = "获取所有备注列表", notes = "m1", produces = "application/json")
    @GetMapping("/recordHairstylisToUsers")
    public Page<RecordHairstylisToUser> getPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(name = "orderBy", defaultValue = "id desc") String orderBy
    ) {
        Page<RecordHairstylisToUser> page = recordHairstylisToUserService.findAll(pageNum, pageSize);
        return page;
    }


    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        // CustomDatePropertyEditor 为自定义日期编辑器
        PropertyEditor dateEditor = new CustomDatePropertyEditor(dateFormat, true, null);
        binder.registerCustomEditor(Date.class, dateEditor);
    }
}
