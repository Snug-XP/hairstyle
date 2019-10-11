package com.gaocimi.flashpig.result;

import java.util.ArrayList;
import java.util.List;

/**
 * API 统一返回状态码
 * Created by zhumaer on 17/5/24.
 */
public enum ResultCode {

    /* 成功状态码 */
    SUCCESS(10000, "成功"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(11000, "参数无效"),

    PARAM_IS_BLANK(12000, "参数为空"),
        PARAM_PASSWORD_IS_BLANK(12001, "密码为空"),
        PARAM_MISSION_CODE_IS_BLANK(12002, "密码为空"),

    PARAM_TYPE_BIND_ERROR(13000, "参数类型错误"),
    PARAM_NOT_COMPLETE(14000, "参数缺失"),

    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(20001, "用户未登录"),
    USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),
    USER_INSUFFICIENT_AMOUNT(20006, "用户积分不足"),
    USER_CONSUME_BEYOND_LIMIT(20007, "达到当日最高限额"),
    USER_CONSUMER_INVITER_ERROR(20008,"邀请者已填写过"),
    USER_CONSUMER_ACHIEVEMENT_HAS_EXISTED(20009,"成就已领取"),

    MISSION_NOT_EXIST(21000,"任务不存在"),
    MISSIONS_NUM_IS_FULL(21001,"任务人数达到最大"),
    MISSIONS_STATUS_IS_CLOSE(21002,"任务状态不是1未开启或已关闭"),

    GOOD_NOT_EXIST(30004, "商品不存在"),
    GOOD_NUM_IS_NOT_ENOUGH(30005,"商品数量不足"),



    PROJECT_NOT_EXIST(22000,"项目不存在"),

    KEY_NUM_IS_NOT_ENOUGH(23000,"key数量不足"),



        /* 业务错误：30001-39999 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST(31001, "某业务出现问题"),



    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR(40001, "系统繁忙，请稍后重试"),

    /* 数据错误：50001-599999 */
    RESULE_DATA_NONE(51000, "数据未找到"),
    DATA_IS_WRONG(52000, "数据有误"),
        DATA_IS_WRONG_operate_exceed_limit(52001, "操作超出单次积分上下限"),
    DATA_ALREADY_EXISTED(53000, "数据已存在"),

    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),

    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS(70001, "无访问权限");

    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

    //校验重复的code值
    public static void main(String[] args) {
        ResultCode[] ApiResultCodes = ResultCode.values();
        List<Integer> codeList = new ArrayList<Integer>();
        for (ResultCode ApiResultCode : ApiResultCodes) {
            if (codeList.contains(ApiResultCode.code)) {
                System.out.println(ApiResultCode.code);
            } else {
                codeList.add(ApiResultCode.code());
            }
        }
    }
}