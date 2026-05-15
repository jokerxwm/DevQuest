package com.devquest.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devquest.question.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
