package com.jangsacartel.biz.notification.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
	private String eventId;
    private String title;
    private String message;
    private String createdAt;
}
