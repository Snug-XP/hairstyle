//package com.gaocimi.flashpig.controller;
//
//import com.gaocimi.flashpig.entity.Article;
//import com.gaocimi.flashpig.model.ArticleExample;
//import com.gaocimi.flashpig.result.ResponseResult;
//import com.gaocimi.flashpig.service.ArticleService;
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
//@Api(value = "管理端文章服务",description = "管理员操作文章相关业务")
//public class ArticleController {
//    @Autowired
//    ArticleService articleService;
//
//    @ApiOperation(value = "添加文章",notes = "m1")
//    @PostMapping("/article")
//    @ResponseStatus(HStatus.CREATED)
//    public int addArticle(@Validated Article articles) {
//        return articleService.insertSelective(articles);
//    }
//
//    @ApiOperation(value = "删除文章",notes = "m1")
//    @DeleteMapping("/article/{articleId}")
//    public int deleteArticle(@NotBlank @PathVariable("articleId") Integer articleId) {
//        return articleService.deleteByPrimaryKey(articleId);
//    }
//
//    @ApiOperation(value = "修改文章",notes = "m1")
//    @PutMapping("/article")
//    public int updateArticle(@Validated Article articles) {
//        return articleService.updateByPrimaryKeySelective(articles);
//    }
//
//
//    @ApiOperation(value = "获取单个文章信息",notes = "m1",produces = "application/json")
//    @GetMapping("/article/{articleId}")
//    public Article getOne( @PathVariable("articleId") Integer articleId) {
//        return articleService.selectByPrimaryKey(articleId);
//    }
//
//    @ApiOperation(value = "获取所有文章列表",notes = "m1",produces = "application/json")
//    @GetMapping("/articles")
//    public Page<Article> getPage(@RequestParam(name="pageNum",defaultValue="0") int pageNum,
//                                 @RequestParam(name="pageSize",defaultValue="10") int pageSize,
//                                 @RequestParam(name="orderBy",defaultValue="id desc") String orderBy
//    ) {
//        Page<Article> page = PageHelper.startPage(pageNum, pageSize, orderBy);
//        ArticleExample example=new ArticleExample();
//        articleService.selectByExample(example);
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
