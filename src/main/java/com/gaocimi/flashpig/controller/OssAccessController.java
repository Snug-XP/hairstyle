package com.gaocimi.flashpig.controller;

import com.aliyun.oss.OSSClient;
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

import com.gaocimi.flashpig.utils.xp.AliOssClient;
import com.gaocimi.flashpig.utils.xp.ImgMessage;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
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
    public Map getOssInfo(String myOpenid) {

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
                    map.put("error", "抱歉，您无权限上传文件！！（openid为"+myOpenid+"）");
                    return map;
                } else {
                    logger.info("id为" + user.getId() + "普通用户“" + user.getName() + "”获取了一个oss临时上传的签名");
                    dir = "user/"+user.getId()+"/";
                }
            } else {
                logger.info("id为" + hairstylist.getId() + "发型师“" + hairstylist.getHairstylistName() + "”获取了一个oss临时上传的签名");
                dir = "hairstylist/"+hairstylist.getId()+"/";
            }
        } else {
            logger.info("id为" + administrator.getId() + "管理员“" + administrator.getName() + "”获取了一个oss临时上传的签名");
            dir = "administrator/"+administrator.getId()+"/";
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
        respMap.put("dir", this.properties.getDir());//当前用户上传指定的前缀
        respMap.put("hostUrl", hostUrl);//当前用户上传指定的前缀
        respMap.put("expire", String.valueOf(expireEndTime / 1000));//签名到期时间的时间戳(不知道怎么用)
        respMap.put("expireDate", expireDate);//签名到期时间

        this.logger.info(JsonUtils.toJson(respMap));
        return respMap;
    }

    //已经采用了前端从后端获取签名后，在前端直传的方式上传图片，这个方法就没用到了
    public ImgMessage saveImg(MultipartFile file) {

        // 创建新实例
        AliOssClient client = new AliOssClient();
        // 连接需要的信息
        client.setAccessKeyId("你的AccessKeyId");
        client.setAccessKeySecret("你的AccessKeySecret");
        client.setEndpoint("你的Endpoint");

        // 返回的文件访问路径
        String url = "";

        try {

            //获取文件的原始名字
            String originalfileName = file.getOriginalFilename();
            // 按日期存储
            //String fileAddress = new Date().toString();
            //重新命名文件
            String suffix = originalfileName.substring(originalfileName.lastIndexOf(".") + 1);
            String fileName = new java.util.Date().getTime() + "-img." + suffix;

            // 获得文件流
            InputStream inputStream = file.getInputStream();

            // 上传到OSS
            client.putObject(this.properties.getBucket(), this.properties.getDir() + fileName, inputStream);

            url += this.properties.getFileHost() + fileName;
            System.out.println("下载url是:" + url);


        } catch (IOException e) {
            e.printStackTrace();
        }

        // 是否有可访问的地址
        if (url.length() < 2) {
            return new ImgMessage("fail", null);
        }
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("image_src", url);
        return new ImgMessage("success", data);
    }
}
