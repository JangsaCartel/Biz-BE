package com.jangsacartel.biz.notification.service;

import com.jangsacartel.biz.notification.domain.NotificationEvent;

public interface NotificationFacade {
	void notify(NotificationEvent event);

	// 게시글 삭제 시 호출
	void deleteNotificationsByPost(int postId);

	// 댓글 삭제 시 호출
	void deleteNotificationsByComment(int commentId);
}
