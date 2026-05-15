package com.devquest.question.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long targetId;

    private Integer targetType;

    private String content;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
