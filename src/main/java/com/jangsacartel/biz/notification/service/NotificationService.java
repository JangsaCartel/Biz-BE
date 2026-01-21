package com.jangsacartel.biz.notification.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.jangsacartel.biz.notification.dto.NotificationDTO;

public interface NotificationService {
    NotificationDTO save(NotificationDTO n);

    List<NotificationDTO> getMyNotifications(int receiverUserId, int page, int size);

    int countUnread(int receiverUserId);

    void markRead(int receiverUserId, long notificationId);

    void markAllRead(int receiverUserId);

    void deleteRead(int receiverUserId);

	SseEmitter connectAndFlush(int userId, String lastEventId);

	boolean trySend(NotificationDTO saved);

	// 삭제 메서드 정의
	void deleteNotificationsByPostId(int postId);
	void deleteNotificationsByCommentId(int commentId);
}
