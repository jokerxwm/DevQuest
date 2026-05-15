package com.devquest.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devquest.question.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
