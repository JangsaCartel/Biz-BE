package com.jangsacartel.biz.notification.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.jangsacartel.biz.global.jwt.util.JwtUtil;
import com.jangsacartel.biz.notification.sse.NotificationHub;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationSseController {

    private final NotificationHub hub;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/test")
    public String test(@RequestParam int userId) {
        hub.sendTest(userId); 
        return "ok";
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("X-Accel-Buffering", "no");

        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없습니다.");
        }

        // Bearer 허용
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        Claims claims = jwtUtil.validateToken(token);
        Number userIdNumber = claims.get("userId", Number.class);
        if (userIdNumber == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 ID를 찾을 수 없습니다.");
        }

        return hub.connect(userIdNumber.intValue());
    }
}
