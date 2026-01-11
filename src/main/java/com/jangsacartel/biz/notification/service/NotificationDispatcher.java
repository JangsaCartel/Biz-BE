package com.jangsacartel.biz.notification.service;

import com.jangsacartel.biz.notification.domain.NotificationEvent;

public interface NotificationDispatcher {
	void dispatch(NotificationEvent event);
}
