package com.gaocimi.flashpig.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.gaocimi.flashpig.configuration.SystemConfigProperty;
import com.gaocimi.flashpig.utils.JsonUtils;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/policy")
public class OssAccessController {

    @Autowired
    private OSSClient client;

    @Autowired
    private SystemConfigProperty properties;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "获取oss信息",notes = "m1")
    @GetMapping("/get")
    public String login(String session_key) {
        if (StringUtils.isBlank(session_key)) {
            return "empty jscode";
        }else{
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, this.properties.getDir());
            String host = "https://" + this.properties.getBucket() + "." + this.properties.getEndpoint();
            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = new byte[0];
            try {
                binaryData = postPolicy.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", this.properties.getAccessId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", this.properties.getDir());
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            this.logger.info(JsonUtils.toJson(respMap));
            return JsonUtils.toJson(respMap);
        }
    }
}
