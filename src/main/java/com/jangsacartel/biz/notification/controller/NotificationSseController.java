package com.jangsacartel.biz.notification.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.jangsacartel.biz.global.jwt.util.JwtUtil;
import com.jangsacartel.biz.notification.domain.NotificationEvent;
import com.jangsacartel.biz.notification.domain.NotificationType;
import com.jangsacartel.biz.notification.service.NotificationFacade;
import com.jangsacartel.biz.notification.service.NotificationService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationSseController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final NotificationFacade notificationFacade;


    private int getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) throw new RuntimeException("No Authorization");
        if (token.startsWith("Bearer ")) token = token.substring(7).trim();
        Claims claims = jwtUtil.validateToken(token);
        Number userIdNumber = claims.get("userId", Number.class);
        if (userIdNumber == null) throw new RuntimeException("No userId");
        return userIdNumber.intValue();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Object myList(HttpServletRequest request,
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "20") int size) {
        int userId = getUserId(request);
        return notificationService.getMyNotifications(userId, page, size);
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            HttpServletRequest request,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {
        int userId = getUserId(request);
        return notificationService.connectAndFlush(userId, lastEventId);
    }
    
    @DeleteMapping("/read")
    public Object deleteRead(HttpServletRequest request) {
        int userId = getUserId(request);
        notificationService.deleteRead(userId);
        return java.util.Map.of("ok", true);
    }


    @GetMapping(value = "/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object unreadCount(HttpServletRequest request) {
        int userId = getUserId(request);
        return java.util.Map.of("unreadCount", notificationService.countUnread(userId));
    }

    @PostMapping("/{notificationId}/read")
    public Object markRead(HttpServletRequest request, @PathVariable long notificationId) {
        int userId = getUserId(request);
        notificationService.markRead(userId, notificationId);
        return java.util.Map.of("ok", true);
    }
    
    @PostMapping("/test") // 테스트용
    public Object test(HttpServletRequest request) {
        int actor = 2;
        int receiver = getUserId(request);

        NotificationEvent event = new NotificationEvent();
        event.setEventId("test-" + System.currentTimeMillis());
        event.setType(NotificationType.COMMENT_CREATED);
        event.setActorUserId(actor);
        event.setReceiverUserId(receiver);
        event.setTitle("댓글 알림");
        event.setMessage("71번 글에 새 댓글이 달렸습니다");
        event.setPostId(71);

        notificationFacade.notify(event);
        return java.util.Map.of("ok", true);
    }

}
