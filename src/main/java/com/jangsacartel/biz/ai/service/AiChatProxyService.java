package com.jangsacartel.biz.ai.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

public interface AiChatProxyService {

    ResponseEntity<String> proxyNode(String nodeId, HttpServletRequest req);

    ResponseEntity<String> proxyTree(HttpServletRequest req);

    ResponseEntity<String> proxyRefresh(HttpServletRequest req);

    void proxyAnswerStream(String body, HttpServletRequest req, HttpServletResponse resp) throws IOException;

    void proxyLlmStream(String body, HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
