package com.jangsacartel.biz.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jangsacartel.biz.notification.domain.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationFacadeImpl implements NotificationFacade {
	
	private final List<NotificationDispatcher> dispatchers;

	@Override
	public void notify(NotificationEvent event) {
		if (event.getActorUserId() != null && event.getActorUserId().equals(event.getReceiverUserId())) {
            return;
        }
        for (NotificationDispatcher dispatcher : dispatchers) {
            dispatcher.dispatch(event);
        }
	}

}
