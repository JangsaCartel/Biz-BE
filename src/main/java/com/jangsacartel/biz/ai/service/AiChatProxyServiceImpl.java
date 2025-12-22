package com.jangsacartel.biz.ai.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AiChatProxyServiceImpl implements AiChatProxyService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${biz.ai.base-url}")
    private String aiBaseUrl;

    @Value("${biz.ai.api-key:}")
    private String aiApiKey;

    @PostConstruct
    private void validateConfig() {
        if (aiBaseUrl == null || aiBaseUrl.trim().isEmpty() || aiBaseUrl.trim().startsWith("${")) {
            throw new IllegalStateException("biz.ai.base-url is not set correctly: " + aiBaseUrl);
        }
    }

    private HttpHeaders buildForwardHeaders(HttpServletRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public ResponseEntity<String> proxyNode(String nodeId, HttpServletRequest req) {
        URI uri = UriComponentsBuilder
            .fromHttpUrl(aiBaseUrl)
            .path("/api/ai/chat/node")
            .queryParam("nodeId", nodeId)
            .build(true)
            .toUri();

        HttpEntity<Void> entity = new HttpEntity<>(buildForwardHeaders(req));
        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @Override
    public ResponseEntity<String> proxyTree(HttpServletRequest req) {
        URI uri = UriComponentsBuilder
            .fromHttpUrl(aiBaseUrl)
            .path("/api/ai/chat/tree")
            .build(true)
            .toUri();

        HttpEntity<Void> entity = new HttpEntity<>(buildForwardHeaders(req));
        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @Override
    public ResponseEntity<String> proxyRefresh(HttpServletRequest req) {
        URI uri = UriComponentsBuilder
            .fromHttpUrl(aiBaseUrl)
            .path("/api/ai/chat/nodes/refresh")
            .build(true)
            .toUri();

        HttpEntity<String> entity = new HttpEntity<>("{}", buildForwardHeaders(req));
        return restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
    }

    @Override
    public void proxyAnswerStream(String body, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareSseResponse(resp);
        URL url = new URL(_join(aiBaseUrl, "/api/ai/chat/answer/stream"));
        relaySsePost(url, body, resp);
    }

    @Override
    public void proxyLlmStream(String body, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareSseResponse(resp);
        URL url = new URL(_join(aiBaseUrl, "/api/ai/chat/llm/stream"));
        relaySsePost(url, body, resp);
    }

    private String _join(String base, String path) {
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        return base + (path.startsWith("/") ? path : ("/" + path));
    }

    private void prepareSseResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/event-stream; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");
        resp.setHeader("X-Accel-Buffering", "no");
        resp.flushBuffer();
    }

    private void relaySsePost(URL targetUrl, String body, HttpServletResponse resp) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        conn.setConnectTimeout(5000);
        conn.setReadTimeout(0);

        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setRequestProperty("Cache-Control", "no-cache");

        if (aiApiKey != null && !aiApiKey.trim().isEmpty() && !aiApiKey.trim().startsWith("${")) {
            conn.setRequestProperty("X-AI-KEY", aiApiKey.trim());
        }

        // request body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int code;
        InputStream aiStream = null;

        try {
            code = conn.getResponseCode();
            aiStream = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();

            // ✅ errorStream이 null인 케이스 방어 (여기서 NPE로 500 자주 남)
            if (aiStream == null) {
                writeSseError(resp, "Upstream returned HTTP " + code + " with empty body");
                return;
            }

            try (BufferedInputStream bis = new BufferedInputStream(aiStream);
                 OutputStream clientOut = resp.getOutputStream()) {

                byte[] buf = new byte[8192];
                int n;

                while ((n = bis.read(buf)) != -1) {
                    clientOut.write(buf, 0, n);
                    clientOut.flush();
                }
            }
        } catch (IOException e) {
            writeSseError(resp, "Proxy error: " + e.getMessage());
        } finally {
            try { if (aiStream != null) aiStream.close(); } catch (Exception ignore) {}
            conn.disconnect();
        }
    }

    private void writeSseError(HttpServletResponse resp, String msg) throws IOException {
        String payload = "{\"done\":true,\"error\":" + toJsonString(msg) + "}\n\n";
        String sse = "data: " + payload;
        resp.getOutputStream().write(sse.getBytes(StandardCharsets.UTF_8));
        resp.getOutputStream().flush();
    }

    private String toJsonString(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
