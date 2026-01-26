package com.jangsacartel.biz.notification.dto;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "알림 이벤트 DTO(내부/테스트 용)")
public class NotificationEventDTO {
	@ApiModelProperty(value = "이벤트 ID", example = "test-1737271234567")
	private String eventId;
	
	@ApiModelProperty(value = "게시글 작성자 ID", example = "1")
	private Integer postUserId;	
	 
	@ApiModelProperty(value = "댓글 작성자 ID", example = "2")
	private Integer commentUserId;	
	
	@ApiModelProperty(value = "제목", example = "댓글 알림")
    private String title;
	
	@ApiModelProperty(value = "메시지", example = "51번 글에 새 댓글이 달렸습니다")
    private String message;
	
	@ApiModelProperty(value = "생성 시각(문자열)", example = "2026-01-19T10:15:30")
    private String createdAt;
    
	@ApiModelProperty(value = "게시글 ID", example = "5")
    private Integer postId;
	
	@ApiModelProperty(value = "댓글 ID", example = "15")
    private Integer commentId;
}
