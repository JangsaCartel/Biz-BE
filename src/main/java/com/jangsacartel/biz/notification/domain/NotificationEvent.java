package com.jangsacartel.biz.notification.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEvent {
	private final String eventId;          
    private final NotificationType type;

    private final Integer receiverUserId;  
    private final Integer actorUserId;     

    private final String title;
    private final String body;
    private final String link;            

    private final Integer postId;        
    private final Integer commentId;       

    private final LocalDateTime createdAt;
}
