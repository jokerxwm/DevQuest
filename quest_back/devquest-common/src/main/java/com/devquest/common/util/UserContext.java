package com.devquest.common.util;

import cn.dev33.satoken.stp.StpUtil;

public class UserContext {

    private UserContext() {}

    public static Long getUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userId;
    }
}
