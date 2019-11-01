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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author xp
 * @date 2019-10-24 22:29:08
 * @description 专辑相关业务
 */
@RestController
@ResponseResult
@Api(value = "专辑操作的相关业务", description = "专辑相关业务")
public class AlbumController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    AlbumService albumService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserService userService;
    @Autowired
    AdministratorService administratorService;

    @ApiOperation(value = "管理员创建专辑")
    @PostMapping("/administrator/addAlbum")
    public Map addAlbum(@RequestParam String myOpenid ,String title, String introduction,String imgUrl) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }

            Album album = new Album();

            album.setAdministrator(administrator);
            album.setTitle(title);
            album.setIntroduction(introduction);
            album.setImgUrl(imgUrl);
            album.setCreateTime(new Date(System.currentTimeMillis()));
            albumService.save(album);

            map.put("message", "专辑上传成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("专辑上传失败！！（后端发生某些错误）");
            map.put("error", "专辑上传失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "删除专辑")
    @DeleteMapping("/album")
    public int deleteAlbum(@PathVariable("albumId") Integer albumId) {
        albumService.delete(albumId);
        return 200;
    }

    @ApiOperation(value = "修改专辑")
    @PutMapping("/album")
    public int updateAlbum(@Validated Album albums) {
        albumService.edit(albums);
        return 200;
    }


    @ApiOperation(value = "获取单个专辑信息", produces = "application/json")
    @GetMapping("/album/getOne/{albumId}")
    public Album getOne(@PathVariable("albumId") Integer albumId) {
        return albumService.findAlbumById(albumId);
    }

    @ApiOperation(value = "获取所有专辑列表")
    @GetMapping("/albums/getAll")
    public Page<Album> getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<Album> page = albumService.findAll(pageNum, pageSize);
        return page;
    }




    @ApiOperation(value = "管理员分页获取自己创建的专辑列表")
    @GetMapping("/album/getMyAlbum")
    public Map getMyCollectionByPage(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if ((administrator == null)) {
                logger.info("非管理员操作！！");
                map.put("error", "对不起，你不是管理员，无权操作！！");
                return map;
            }

            List<Album> tempAlbumList = administrator.getAlbumList();
            List<Album> resultAlbumList = new ArrayList<>();

            if (tempAlbumList == null) {
                logger.info("你还没有创建的专辑哦~");
                map.put("message", "你还没有创建的专辑哦~");
                return map;
            }

            // 按时间倒序排序
            Collections.sort(tempAlbumList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            //获取所求页数的专辑数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempAlbumList.size(); i++) {
                resultAlbumList.add(tempAlbumList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Album> page = new PageImpl<>(resultAlbumList, pageable, tempAlbumList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己创建的专辑列表失败！！（后端发生某些错误）");
            map.put("error", "获取自己创建的专辑列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }
}
