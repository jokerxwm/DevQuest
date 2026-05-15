package com.devquest.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_content_audit")
public class AiContentAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long targetId;

    private String targetType;

    private Integer result;

    private String reason;

    private BigDecimal confidence;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
