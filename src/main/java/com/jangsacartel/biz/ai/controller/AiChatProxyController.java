package com.jangsacartel.biz.ai.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jangsacartel.biz.ai.service.AiChatProxyService;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatProxyController {

    private final AiChatProxyService proxyService;

    public AiChatProxyController(AiChatProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GetMapping("/node")
    public ResponseEntity<String> proxyNode(@RequestParam("nodeId") String nodeId, HttpServletRequest req) {
        return proxyService.proxyNode(nodeId, req);
    }

    @GetMapping("/tree")
    public ResponseEntity<String> proxyTree(HttpServletRequest req) {
        return proxyService.proxyTree(req);
    }

    @PostMapping("/nodes/refresh")
    public ResponseEntity<String> proxyRefresh(HttpServletRequest req) {
        return proxyService.proxyRefresh(req);
    }

    @PostMapping(value = "/answer/stream", consumes = "application/json")
    public void proxyAnswerStream(@RequestBody String body, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        proxyService.proxyAnswerStream(body, req, resp);
    }

    @PostMapping(value = "/llm/stream", consumes = "application/json")
    public void proxyLlmStream(@RequestBody String body, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        proxyService.proxyLlmStream(body, req, resp);
    }
}
