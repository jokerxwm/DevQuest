package com.devquest.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.notification.entity.Notification;
import com.devquest.notification.mapper.NotificationMapper;
import com.devquest.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public Notification createNotification(Long receiverId, Long senderId, String type,
                                            String title, String content, Long targetId, String targetType) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetId(targetId);
        notification.setTargetType(targetType);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
        return notification;
    }

    @Override
    public PageResult<Notification> getUserNotifications(Long userId, int page, int size, String type) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, userId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notification::getType, type);
        }
        wrapper.orderByDesc(Notification::getCreatedAt);

        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, userId)
                .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getReceiverId().equals(userId)) {
            throw new BizException("通知不存在");
        }

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, notificationId)
                .set(Notification::getIsRead, 1);
        notificationMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getReceiverId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1);
        notificationMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getReceiverId().equals(userId)) {
            throw new BizException("通知不存在");
        }
        notificationMapper.deleteById(notificationId);
    }
}
