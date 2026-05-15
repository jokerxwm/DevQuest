package com.devquest.user.service;

import com.devquest.common.dto.LoginDTO;
import com.devquest.common.dto.RegisterDTO;
import com.devquest.user.entity.User;

import java.util.Map;

public interface UserService {

    Map<String, Object> register(RegisterDTO dto);

    Map<String, Object> login(LoginDTO dto);

    void logout();

    User getUserInfo(Long userId);

    User getUserByUsername(String username);

    void updateProfile(Long userId, User user);

    void followUser(Long followerId, Long followingId);

    void unfollowUser(Long followerId, Long followingId);

    boolean isFollowing(Long followerId, Long followingId);

    long getFollowerCount(Long userId);

    long getFollowingCount(Long userId);
}
