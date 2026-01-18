package com.jangsacartel.biz.ai.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangsacartel.biz.ai.dto.AiChatAnswerStreamRequestDto;
import com.jangsacartel.biz.ai.dto.AiChatLlmStreamRequestDto;
import com.jangsacartel.biz.ai.service.AiChatProxyService;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/chat")
@Api(tags = "AI 채팅 프록시 컨트롤러")
public class AiChatProxyController {

    private final AiChatProxyService proxyService;
    private final ObjectMapper objectMapper;

    @ApiOperation(
        value = "챗봇 노드 조회(프록시)",
        notes = "nodeId에 해당하는 챗봇 노드(JSON)를 AI 서버로부터 프록시하여 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공 (JSON 문자열 반환)"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/node")
    public ResponseEntity<String> proxyNode(
        @ApiParam(value = "조회할 노드 ID", required = true, example = "ROOT")
        @RequestParam("nodeId") String nodeId,
        HttpServletRequest req
    ) {
        return proxyService.proxyNode(nodeId, req);
    }

    
    @ApiOperation(
        value = "챗봇 트리 조회(프록시)",
        notes = "전체 챗봇 트리(JSON)를 AI 서버로부터 프록시하여 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공 (JSON 문자열 반환)"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/tree")
    public ResponseEntity<String> proxyTree(HttpServletRequest req) {
        return proxyService.proxyTree(req);
    }

    
    @ApiOperation(
        value = "챗봇 노드 리프레시(프록시)",
        notes = "AI 서버의 노드/트리 캐시(또는 리소스)를 갱신합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공 (JSON 문자열 반환)"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/nodes/refresh")
    public ResponseEntity<String> proxyRefresh(HttpServletRequest req) {
        return proxyService.proxyRefresh(req);
    }

    
    @ApiOperation(
        value = "답변 생성 스트리밍(SSE 프록시)",
        notes = "요청 바디(JSON)를 AI 서버로 전달하고 SSE로 스트리밍합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공 (SSE 스트림 시작)"),
        @ApiResponse(code = 400, message = "잘못된 요청 (JSON 형식 오류 등)"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    
    @PostMapping(value = "/answer/stream", consumes = "application/json", produces = "text/event-stream")
    public void proxyAnswerStream(
        @RequestBody AiChatAnswerStreamRequestDto body,
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws IOException {
        String json = objectMapper.writeValueAsString(body);
        proxyService.proxyAnswerStream(json, req, resp);
    }

    @ApiOperation(value = "자유 질문 스트리밍(SSE 프록시)")
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공 (SSE 스트림 시작)"),
        @ApiResponse(code = 400, message = "잘못된 요청 (JSON 형식 오류 등)"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping(value = "/llm/stream", consumes = "application/json", produces = "text/event-stream")
    public void proxyLlmStream(
        @RequestBody AiChatLlmStreamRequestDto body,
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws IOException {
        String json = objectMapper.writeValueAsString(body);
        proxyService.proxyLlmStream(json, req, resp);
    }
}
