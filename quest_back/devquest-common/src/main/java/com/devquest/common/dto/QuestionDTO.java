package com.devquest.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionDTO implements Serializable {

    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题最长255个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private List<Long> tagIds;
}
