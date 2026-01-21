package com.jangsacartel.biz.map.controller;

import com.jangsacartel.biz.map.dto.KakaoMapApiKeyResponse;
import com.jangsacartel.biz.map.service.GeoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/map")
public class MapController {

    @Value("${kakao.map.api}")
    private String kakaoMapApiKey;

    @Autowired
    private GeoDataService geoDataService;

    @GetMapping("/key")
    public KakaoMapApiKeyResponse getKakaoMapApiKey() {
        return new KakaoMapApiKeyResponse(kakaoMapApiKey);
    }

    @GetMapping("/boundaries")
    public ResponseEntity<Map<String, Object>> getBoundaries(
            @RequestParam double minLat, @RequestParam double minLng,
            @RequestParam double maxLat, @RequestParam double maxLng) {
        Map<String, Object> featureCollectionMap = geoDataService.findIntersectingFeatures(minLat, minLng, maxLat, maxLng);
        return ResponseEntity.ok(featureCollectionMap);
    }
}
