package com.jangsacartel.biz.notification.dto;

import java.time.LocalDateTime;

import io.swagger.annotations.*;
import lombok.Data;

@Data
@ApiModel (description = "알림 DTO")
public class NotificationDTO {
	@ApiModelProperty(value = "알림 ID", example = "101")
    private Long notificationId;

	@ApiModelProperty(value = "수신자 유저 ID", example = "1")
    private Integer receiverUserId;
	
	@ApiModelProperty(value = "행위자(발신자) 유저 ID", example = "2")
    private Integer actorUserId;

	@ApiModelProperty(value = "알림 타입", example = "COMMENT_CREATED")
    private String type;
	
	@ApiModelProperty(value = "알림 제목", example = "댓글 알림")
    private String title;
	
	@ApiModelProperty(value = "알림 내용", example = "71번 글에 새 댓글이 달렸습니다")
    private String message;

	@ApiModelProperty(value = "관련 게시글 ID", example = "71")
    private Integer postId;
	
	@ApiModelProperty(value = "관련 댓글 ID", example = "55")
    private Integer commentId;   

	@ApiModelProperty(value = "읽음 여부", example = "false")
    private Boolean isRead;   
	
	@ApiModelProperty(value = "생성 시각", example = "2026-01-19T10:15:30")
    private LocalDateTime createdAt;
}
