package com.devquest.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer answerCount;

    private Integer voteCount;

    private Integer favoriteCount;

    private Integer status;

    private Long acceptedAnswerId;

    private Integer isTop;

    private LocalDateTime lastAnswerTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
