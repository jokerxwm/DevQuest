package com.devquest.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_EXISTS(1004, "用户已存在"),
    EMAIL_EXISTS(1005, "邮箱已被注册"),

    QUESTION_NOT_FOUND(2001, "问题不存在"),
    ANSWER_NOT_FOUND(2002, "回答不存在"),
    ALREADY_VOTED(2003, "已经投过票了"),
    ALREADY_FAVORITED(2004, "已经收藏过了"),

    AI_SERVICE_ERROR(3001, "AI服务异常"),
    AI_RATE_LIMIT(3002, "AI请求过于频繁"),

    SYSTEM_ERROR(9999, "系统内部错误");

    private final int code;
    private final String msg;
}
