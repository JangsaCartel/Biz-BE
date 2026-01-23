package com.jangsacartel.biz.notification.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.jangsacartel.biz.global.jwt.util.JwtUtil;
import com.jangsacartel.biz.notification.service.NotificationFacade;
import com.jangsacartel.biz.notification.service.NotificationService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Api(tags = "알림 컨트롤러")
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

    @ApiOperation(
        value = "내 알림 목록 조회",
        notes = "로그인 사용자의 알림 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Object myList(
        HttpServletRequest request,
        @ApiParam(value = "페이지(1부터)", example = "1") @RequestParam(defaultValue = "1") int page,
        @ApiParam(value = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    ) {
        int userId = getUserId(request);
        return notificationService.getMyNotifications(userId, page, size);
    }

    @ApiOperation(
        value = "알림 SSE 구독",
        notes =
            "알림을 SSE(text/event-stream)로 스트리밍합니다.\n" +
            "- Swagger UI에서는 SSE 스트리밍이 잘 보이지 않을 수 있으니 curl로 테스트 권장\n\n" +
            "예시:\n" +
            "1) 기본 구독(Last-Event-ID 없이) \\\n" +
            "- 가장 일반적인 테스트 방법\\\n" +
            "- 연결이 끊기지 않고 대기 상태로 유지됩니다. \\\n" +
            "curl -N -H \"Accept: text/event-stream\" \\\n" +
            "  -H \"Authorization: Bearer {ACCESS_TOKEN}\" \\\n" +
            "  GET /api/notifications/stream" +
            "\n" +
            "2) Last-Event-ID 헤더 전달 확인(개발/디버그용) \\\n" +
            "- 서버가 Last-Event-ID 값을 받는지 확인하는 용도(임의 값 가능) \\\n" +
            "curl -N -H \"Accept: text/event-stream\" \\\n" +
            "  -H \"Authorization: Bearer {ACCESS_TOKEN}\" \\\n" +
            "  -H \"Last-Event-ID: test-1700000000000(또는 임의의 값)\" \\\n" +
            "  GET /api/notifications/stream"
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공 (SSE 스트림 시작)"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
        HttpServletRequest request,
        @ApiParam(value = "마지막 이벤트 ID(재연결 시)", required = false)
        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {
        int userId = getUserId(request);
        
        // System.out.println("[SSE] userId=" + userId + ", Last-Event-ID=" + lastEventId);
        
        return notificationService.connectAndFlush(userId, lastEventId);
    }

    @ApiOperation(value = "읽은 알림 전체 삭제", notes = "로그인 사용자의 '읽음(isRead=true)' 알림을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공")
    })
    @DeleteMapping("/read")
    public Map<String, Object> deleteRead(HttpServletRequest request) {
        int userId = getUserId(request);
        notificationService.deleteRead(userId);
        return Map.of("ok", true);
    }

    @ApiOperation(value = "미확인 알림 개수 조회", notes = "로그인 사용자의 unread count를 반환합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공")
    })
    @GetMapping(value = "/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> unreadCount(HttpServletRequest request) {
        int userId = getUserId(request);
        return Map.of("unreadCount", notificationService.countUnread(userId));
    }

    @ApiOperation(value = "알림 읽음 처리", notes = "특정 알림을 읽음 처리합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 404, message = "알림 없음")
    })
    @PostMapping("/{notificationId}/read")
    public Map<String, Object> markRead(
        HttpServletRequest request,
        @ApiParam(value = "알림 ID", example = "101") @PathVariable long notificationId
    ) {
        int userId = getUserId(request);
        notificationService.markRead(userId, notificationId);
        return Map.of("ok", true);
    }

//    @ApiOperation(
//        value = "알림 테스트 발송(개발용)",
//        notes = "현재 로그인 사용자에게 테스트 알림을 1건 발송합니다."
//    )
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "성공")
//    })
//    @PostMapping("/test")
//    public Map<String, Object> test(HttpServletRequest request) {
//        int actor = 2;
//        int receiver = getUserId(request);
//
//        NotificationEvent event = new NotificationEvent();
//        event.setEventId("test-" + System.currentTimeMillis());
//        event.setType(NotificationType.COMMENT_CREATED);
//        event.setActorUserId(actor);
//        event.setReceiverUserId(receiver);
//        event.setTitle("댓글 알림");
//        event.setMessage("71번 글에 새 댓글이 달렸습니다");
//        event.setPostId(71);
//
//        notificationFacade.notify(event);
//        return Map.of("ok", true);
//    }
}
