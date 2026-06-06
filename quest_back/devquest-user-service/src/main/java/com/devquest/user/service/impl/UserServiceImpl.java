package com.devquest.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devquest.common.dto.LoginDTO;
import com.devquest.common.dto.RegisterDTO;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.ResultCode;
import com.devquest.user.entity.User;
import com.devquest.user.entity.UserFollow;
import com.devquest.user.mapper.UserFollowMapper;
import com.devquest.user.mapper.UserMapper;
import com.devquest.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserFollowMapper userFollowMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_KEY = "user:info:";
    private static final long USER_CACHE_TTL = 1800;

    @Override
    @Transactional
    public Map<String, Object> register(RegisterDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BizException(ResultCode.USER_EXISTS);
        }

        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, dto.getEmail());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BizException(ResultCode.EMAIL_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setReputation(0);
        user.setQuestionCount(0);
        user.setAnswerCount(0);
        user.setStatus(1);
        user.setRole("USER");
        userMapper.insert(user);

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, dto.getEmail())
                .or()
                .eq(User::getUsername, dto.getEmail());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() == 0) {
            throw new BizException(ResultCode.USER_DISABLED);
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.USER_PASSWORD_ERROR);
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public User getUserInfo(Long userId) {
        String cacheKey = USER_CACHE_KEY + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof User) {
            return (User) cached;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(null);
        redisTemplate.opsForValue().set(cacheKey, user, USER_CACHE_TTL, TimeUnit.SECONDS);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, User updateData) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        if (updateData.getNickname() != null) user.setNickname(updateData.getNickname());
        if (updateData.getAvatar() != null) user.setAvatar(updateData.getAvatar());
        if (updateData.getBio() != null) user.setBio(updateData.getBio());
        if (updateData.getWebsite() != null) user.setWebsite(updateData.getWebsite());
        if (updateData.getGithub() != null) user.setGithub(updateData.getGithub());

        userMapper.updateById(user);
        redisTemplate.delete(USER_CACHE_KEY + userId);
    }

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BizException("不能关注自己");
        }

        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
                .eq(UserFollow::getFollowingId, followingId);
        if (userFollowMapper.selectCount(wrapper) > 0) {
            return;
        }

        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowMapper.insert(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
                .eq(UserFollow::getFollowingId, followingId);
        userFollowMapper.delete(wrapper);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
                .eq(UserFollow::getFollowingId, followingId);
        return userFollowMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long getFollowerCount(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId);
        return userFollowMapper.selectCount(wrapper);
    }

    @Override
    public long getFollowingCount(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId);
        return userFollowMapper.selectCount(wrapper);
    }

    @Override
    public List<User> getFollowers(Long userId, int page, int size) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId)
                .orderByDesc(UserFollow::getCreatedAt);
        List<UserFollow> follows = userFollowMapper.selectList(wrapper);
        if (follows.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userIds = follows.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(UserFollow::getFollowerId)
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    @Override
    public List<User> getFollowing(Long userId, int page, int size) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId)
                .orderByDesc(UserFollow::getCreatedAt);
        List<UserFollow> follows = userFollowMapper.selectList(wrapper);
        if (follows.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userIds = follows.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(UserFollow::getFollowingId)
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        users.forEach(u -> u.setPassword(null));
        return users;
    }
}
