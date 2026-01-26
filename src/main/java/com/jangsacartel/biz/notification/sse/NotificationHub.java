package com.jangsacartel.biz.notification.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class NotificationHub {

    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(int userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> { emitters.remove(userId); emitter.complete(); });
        emitter.onError(e -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }
        return emitter;
    }

    public boolean isConnected(int userId) {
        return emitters.containsKey(userId);
    }

    public boolean sendToUser(int userId, Object payload, String eventId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return false;

        try {
            emitter.send(SseEmitter.event()
                .id(eventId)
                .name("notification")
                .data(payload));
            return true;
        } catch (Exception e) {
            emitters.remove(userId);
            try { emitter.completeWithError(e); } catch (Exception ignore) {}
            return false;
        }
    }


}
