package com.jangsacartel.biz.notification.service;

import org.springframework.stereotype.Service;

import com.jangsacartel.biz.notification.domain.NotificationEvent;
import com.jangsacartel.biz.notification.dto.NotificationDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationFacadeImpl implements NotificationFacade {

    private final NotificationService notificationService; 

    @Override
    public void notify(NotificationEvent event) {
        // self notify 차단
        if (event.getActorUserId() != null && event.getActorUserId().equals(event.getReceiverUserId())) {
            return;
        }

        NotificationDTO dto = new NotificationDTO();
        dto.setReceiverUserId(event.getReceiverUserId());
        dto.setActorUserId(event.getActorUserId());
        dto.setType(event.getType().name());
        dto.setTitle(event.getTitle());
        dto.setMessage(event.getMessage());
        dto.setPostId(event.getPostId());
        dto.setCommentId(event.getCommentId());

        NotificationDTO saved = notificationService.save(dto);

        notificationService.trySend(saved);
    }

    // 게시글 삭제 연결
    @Override
    public void deleteNotificationsByPost(int postId) {
        notificationService.deleteNotificationsByPostId(postId);
    }

    // 댓글 삭제 연결
    @Override
    public void deleteNotificationsByComment(int commentId) {
        notificationService.deleteNotificationsByCommentId(commentId);
    }
}
