package com.devquest.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerDTO implements Serializable {

    @NotNull(message = "问题ID不能为空")
    private Long questionId;

    @NotBlank(message = "内容不能为空")
    private String content;
}
