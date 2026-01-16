package com.jangsacartel.biz.notification.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long notificationId;

    private Integer receiverUserId;
    private Integer actorUserId;

    private String type;         
    private String title;
    private String message;

    private Integer postId;
    private Integer commentId;   

    private Boolean isRead;      
    private LocalDateTime createdAt;
}
