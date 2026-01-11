package com.jangsacartel.biz.notification.sse;
import com.jangsacartel.biz.notification.dto.NotificationEventDTO;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class NotificationHub {

    // userId -> emitter
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(int userId) {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("[SSE] completed userId={}", userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
            emitter.complete();
            log.info("[SSE] timeout userId={}", userId);
        });

        emitter.onError(e -> {
            emitters.remove(userId);
            log.warn("[SSE] error userId={}, err={}", userId, e.toString());
        });

        // 연결 직후 더미 이벤트
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("ok"));
        } catch (IOException e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    // 특정 유저에게 알림 1건 푸시
    public void sendToUser(int userId, Object payload) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(payload));
        } catch (IOException e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }
    }
    
    // 테스트용 더미 알림 전송
    public void sendTest(int userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            NotificationEventDTO payload =
                new NotificationEventDTO("test-1", "테스트", "알림 테스트입니다", "now");

            emitter.send(SseEmitter.event()
                .name("notification")
                .data(payload) 
            );
        } catch (Exception e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }
    }
}
