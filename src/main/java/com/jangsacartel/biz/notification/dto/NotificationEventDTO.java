package com.jangsacartel.biz.notification.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
	private String eventId;
	private Integer postUserId;	
	private Integer commentUserId;	
	
	
    private String title;
    private String message;
    private String createdAt;
    
    private Integer postId;
    private Integer commentId;
}
