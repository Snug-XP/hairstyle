package com.gaocimi.flashpig.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.gaocimi.flashpig.configuration.SystemConfigProperty;
import com.gaocimi.flashpig.entity.Administrator;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.service.AdministratorService;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.utils.JsonUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@Api(value = "阿里云OSS服务", description = "阿里云OSS操作相关业务")
public class OssAccessController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OSSClient client;
    @Autowired
    private SystemConfigProperty properties;
    @Autowired
    private UserService userService;
    @Autowired
    private HairstylistService hairstylistService;
    @Autowired
    private AdministratorService administratorService;


    @ApiOperation(value = "获取oss直传所需的信息（仅允许数据库中存在的用户、发型师和管理员获取oss签名）", notes = "[采用JavaScript客户端直接签名](https://help.aliyun.com/document_detail/31925.html?spm=a2c4g.11186623.2.11.16076e284W2rpY#concept-frd-4gy-5db)" +
            "时，AccessKeyID和AcessKeySecret会暴露在前端页面，因此存在严重的安全隐患。因此，OSS提供了服务端签名后直传的方案")
    @GetMapping("/policy/getOssInfo")
    public Map getOssInfo(@RequestParam String myOpenid) {

        Map map = new HashMap();
        String dir = null;//设置当前用户上传指定的前缀，必须以斜线结尾,类似目录（OSS不存在多级目录，但是可以模拟）

        //权限判断（仅允许数据库中存在的用户、发型师和管理员获取oss签名）
        Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
        if (administrator == null) {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                User user = userService.findUserByOpenid(myOpenid);
                if (user == null) {
                    logger.info("抱歉，您无权限上传文件！！");
                    map.put("error", "抱歉，您无权限上传文件！！（openid为" + myOpenid + "）");
                    return map;
                } else {
                    logger.info("id为" + user.getId() + "普通用户“" + user.getName() + "”获取了一个oss临时上传的签名");
                    dir = "user/" + user.getId() + "/";
                }
            } else {
                logger.info("id为" + hairstylist.getId() + "发型师“" + hairstylist.getHairstylistName() + "”获取了一个oss临时上传的签名");
                dir = "hairstylist/" + hairstylist.getId() + "/";
            }
        } else {
            logger.info("id为" + administrator.getId() + "管理员“" + administrator.getName() + "”获取了一个oss临时上传的签名");
            dir = "administrator/" + administrator.getId() + "/";
        }

        //第一步，构造policy
        long expireTime = 30;//30秒时间限制
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expireDate = new Date(expireEndTime);//到期时间
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 10485760);//允许上传的文件大小限制
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, this.properties.getDir());//上传目录
        String hostUrl = "https://" + this.properties.getBucket() + "." + this.properties.getEndpoint();
        String postPolicy = client.generatePostPolicy(expireDate, policyConds);//给policyConds添加过期时间并json序列化（格式iso8601:"yyyy-MM-dd'T'HH:mm:ss.fff'Z'"）
        //第二步 将policy 的json字符串进行base64编码
        byte[] binaryData = new byte[0];
        try {
            binaryData = postPolicy.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        //第三步，生成签名
        String postSignature = client.calculatePostSignature(postPolicy);
        //以下返回给前端表单域或者阿里云OSS的js api
        Map respMap = new HashMap();
        respMap.put("accessid", this.properties.getAccessId());
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);//对应的签名
        respMap.put("dir", this.properties.getDir() + dir);//当前用户上传指定的前缀
        respMap.put("hostUrl", hostUrl);//当前用户上传指定的前缀
        respMap.put("expire", String.valueOf(expireEndTime / 1000));//签名到期时间的时间戳(不知道怎么用)
        respMap.put("expireDate", expireDate);//签名到期时间

        this.logger.info(JsonUtils.toJson(respMap));
        return respMap;
    }


    //已经采用了前端从后端获取签名后，在前端直传的方式上传图片，这个方法就没用到了
    @ApiOperation(value = "上传文件")
    @PostMapping("/oss/saveObject")
    public Map saveObject(File file , String fileName) {
        Map map = new HashMap();

        // 返回的文件访问路径
        String url = "";

        try {

            //获取文件的原始名字
            String originalfileName = file.getName();
            // 按日期存储
            //String fileAddress = new Date().toString();
            //重新命名文件
            String suffix = originalfileName.substring(originalfileName.lastIndexOf(".") + 1);
            fileName = fileName +"."+ suffix;

            // 获得文件流
            InputStream inputStream = new FileInputStream(file);

            // 上传到OSS
            map.put("result", client.putObject(this.properties.getBucket(), this.properties.getDir() + fileName, inputStream));

            url += this.properties.getFileHost() + "/" + this.properties.getDir() + fileName;
            System.out.println("下载url是:" + url);


        } catch (IOException e) {
            e.printStackTrace();
            logger.info("上传文件“"+file.getName()+"”失败！");
            map.put("error","上传文件“"+file.getName()+"”失败！");
            return map;
        }catch ( OSSException e){
            logger.info("上传文件“"+file.getName()+"”失败！\n"+e.getMessage());
            map.put("error","上传文件“"+file.getName()+"”失败！"+e.getMessage());
            e.printStackTrace();
            return map;
        }

        map.put("image_src", url);
        return map;
    }


    @ApiOperation(value = "删除文件（仅允许数据库中存在的用户、发型师和管理员删除文件）...记得关闭url接口", notes = "文件url格式不包括域名")
    @DeleteMapping("/oss/deleteObject")
    public Map deleteObject(@RequestParam String myOpenid,@RequestParam String imgUrl) {
        Map map = new HashMap();

        try {
            //权限判断（仅允许数据库中存在的用户、发型师和管理员获取oss签名）
            Administrator administrator = administratorService.findAdministratorByOpenid(myOpenid);
            if (administrator == null) {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                if (hairstylist == null) {
                    User user = userService.findUserByOpenid(myOpenid);
                    if (user == null) {
                        logger.info("抱歉，您无权限删除文件！！");
                        map.put("error", "抱歉，您无权限删除文件！！（openid为" + myOpenid + "）");
                        return map;
                    } else {
                        logger.info("id为" + user.getId() + "普通用户“" + user.getName() + "”删除了oss的照片：" + imgUrl);
                    }
                } else {
                    logger.info("id为" + hairstylist.getId() + "发型师“" + hairstylist.getHairstylistName() + "”删除了oss的照片：" + imgUrl);
                }
            } else {
                logger.info("id为" + administrator.getId() + "管理员“" + administrator.getName() + "”删除了oss的照片：" + imgUrl);
            }


            if (!client.doesObjectExist(properties.getBucket(), imgUrl)) {
                logger.info("要删除的图片不存在（" + imgUrl + "）");
                map.put("error", "图片不存在");
                return map;
            }
            client.deleteObject(this.properties.getBucket(), imgUrl);
            map.put("message", "图片删除成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
            map.put("error", e.getMessage());
        }
        return map;
    }

    @ApiOperation(value = "验证对象是否存在")
    @PostMapping("/oss/verifyObjectExist")
    public Map verifyExist(@RequestParam String imgUrl) {
        Map map = new HashMap();
        try {
            map.put("result", client.doesObjectExist(properties.getBucket(), imgUrl));
        } catch (Exception e) {
            logger.error(e.getMessage());
            map.put("error", e.getMessage());
        }
        return map;
    }

}
