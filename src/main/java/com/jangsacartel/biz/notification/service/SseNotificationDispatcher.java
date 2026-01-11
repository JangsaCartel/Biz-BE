package com.jangsacartel.biz.notification.service;

import org.springframework.stereotype.Component;

import com.jangsacartel.biz.notification.domain.NotificationEvent;
import com.jangsacartel.biz.notification.sse.NotificationHub;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseNotificationDispatcher implements NotificationDispatcher {
	
	private final NotificationHub hub;
	
	@Override
	public void dispatch(NotificationEvent event) {
		hub.sendToUser(event.getReceiverUserId(), event);
	}
	
}
