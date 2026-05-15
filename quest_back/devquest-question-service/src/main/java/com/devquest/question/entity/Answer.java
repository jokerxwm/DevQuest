package com.devquest.question.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer")
public class Answer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;

    private Long userId;

    private String content;

    private Integer voteCount;

    private Integer isAccepted;

    private Integer isAiGenerated;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
