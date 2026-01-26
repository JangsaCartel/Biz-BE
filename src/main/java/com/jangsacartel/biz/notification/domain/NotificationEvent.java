package com.jangsacartel.biz.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String eventId;
    private NotificationType type;

    private Integer receiverUserId;
    private Integer actorUserId;

    private String title;
    private String message;
    private String createdAt;

    private Integer postId;
    private Integer commentId;
}
