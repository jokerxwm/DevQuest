package com.devquest.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_follow")
public class UserFollow {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long followerId;

    private Long followingId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
