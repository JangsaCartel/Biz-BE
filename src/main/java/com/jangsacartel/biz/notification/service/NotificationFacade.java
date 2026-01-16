package com.jangsacartel.biz.notification.service;

import com.jangsacartel.biz.notification.domain.NotificationEvent;

public interface NotificationFacade {
	void notify(NotificationEvent event);
}
