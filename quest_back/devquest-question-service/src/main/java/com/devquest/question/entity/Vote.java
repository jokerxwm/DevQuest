package com.devquest.question.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vote")
public class Vote {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long targetId;

    private Integer targetType;

    private Integer voteType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
