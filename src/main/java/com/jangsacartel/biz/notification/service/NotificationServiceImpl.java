package com.jangsacartel.biz.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.jangsacartel.biz.notification.dto.NotificationDTO;
import com.jangsacartel.biz.notification.mapper.NotificationMapper;
import com.jangsacartel.biz.notification.sse.NotificationHub;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationHub hub;

    @Override
    public NotificationDTO save(NotificationDTO n) {
        if (n.getIsRead() == null) n.setIsRead(false);
        if (n.getCreatedAt() == null) n.setCreatedAt(LocalDateTime.now());

        notificationMapper.insertNotification(n); // useGeneratedKeys로 notificationId 채워짐
        return n;
    }

    @Override
    public boolean trySend(NotificationDTO saved) {
        if (saved == null || saved.getNotificationId() == null) return false;

        return hub.sendToUser(
            saved.getReceiverUserId(),
            toSsePayload(saved),
            "n-" + saved.getNotificationId()
        );
    }

    @Override
    public SseEmitter connectAndFlush(int userId, String lastEventId) {
        SseEmitter emitter = hub.connect(userId);

        Long lastId = parseLastEventId(lastEventId); // 없으면 null
        flushUnread(userId, 100, lastId);

        return emitter;
    }

    private void flushUnread(int userId, int batchSize, Long afterNotificationId) {
        while (true) {
            List<NotificationDTO> unread =
                notificationMapper.findUnreadAfterId(userId, afterNotificationId, batchSize);

            if (unread == null || unread.isEmpty()) return;

            for (NotificationDTO n : unread) {
                boolean sent = hub.sendToUser(
                    userId,
                    toSsePayload(n),
                    "n-" + n.getNotificationId()
                );
                if (!sent) return; // 끊기면 stop
                afterNotificationId = n.getNotificationId(); // 다음 배치 기준 업데이트
            }

            if (unread.size() < batchSize) return;
        }
    }

    private Long parseLastEventId(String lastEventId) {
        if (lastEventId == null || lastEventId.isBlank()) return null;
        // "n-123" 형태
        try {
            String s = lastEventId.trim();
            if (s.startsWith("n-")) s = s.substring(2);
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Object toSsePayload(NotificationDTO n) {
        return java.util.Map.of(
            "eventId", "n-" + n.getNotificationId(),
            "notificationId", n.getNotificationId(),
            "title", n.getTitle(),
            "message", n.getMessage(),
            "createdAt", n.getCreatedAt() == null ? "" : n.getCreatedAt().toString(),
            "postId", n.getPostId(),
            "isRead", n.getIsRead() != null && n.getIsRead()
        );
    }

    // ========= 기존 REST =========

    @Override
    public List<NotificationDTO> getMyNotifications(int receiverUserId, int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        int offset = (safePage - 1) * safeSize;
        return notificationMapper.findByReceiver(receiverUserId, offset, safeSize);
    }

    @Override
    public int countUnread(int receiverUserId) {
        return notificationMapper.countUnread(receiverUserId);
    }

    @Override
    public void markRead(int receiverUserId, long notificationId) {
        notificationMapper.markRead(notificationId, receiverUserId);
    }

    @Override
    public void markAllRead(int receiverUserId) {
        notificationMapper.markAllRead(receiverUserId);
    }

    // ✅ A안: “읽은 알림 삭제” 버튼용
    @Override
    public void deleteRead(int receiverUserId) {
        notificationMapper.deleteRead(receiverUserId);
    }
}
