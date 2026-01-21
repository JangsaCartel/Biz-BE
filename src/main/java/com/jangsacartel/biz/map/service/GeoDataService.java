package com.jangsacartel.biz.map.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.LinearRing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeoDataService {

    private static final Logger logger = LoggerFactory.getLogger(GeoDataService.class);
    private List<Map<String, Object>> allFeatures;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadGeoJsonData() {
        logger.info("Loading GeoJSON data for administrative districts...");
        try (InputStream inputStream = new ClassPathResource("data/HangJeongDong_ver20250401.geojson").getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> featureCollectionMap = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
            this.allFeatures = (List<Map<String, Object>>) featureCollectionMap.get("features");
            logger.info("Successfully loaded {} features.", allFeatures.size());
        } catch (Exception e) {
            logger.error("Failed to load or parse GeoJSON file.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> findIntersectingFeatures(double minLat, double minLng, double maxLat, double maxLng) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "FeatureCollection");

        if (allFeatures == null) {
            logger.warn("GeoJSON features not loaded. Returning empty collection.");
            result.put("features", new ArrayList<>());
            return result;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coords = new Coordinate[] {
                new Coordinate(minLng, minLat), new Coordinate(maxLng, minLat),
                new Coordinate(maxLng, maxLat), new Coordinate(minLng, maxLat),
                new Coordinate(minLng, minLat)
        };
        Polygon requestBounds = geometryFactory.createPolygon(coords);

        List<Map<String, Object>> intersectingFeatures = allFeatures.parallelStream()
                .filter(feature -> {
                    Map<String, Object> geometryMap = (Map<String, Object>) feature.get("geometry");
                    if (geometryMap == null) return false;

                    try {
                        Geometry featureGeometry = createGeometryFromMap(geometryMap, geometryFactory);
                        return requestBounds.intersects(featureGeometry);
                    } catch (Exception e) {
                        
                        return false;
                    }
                })
                .collect(Collectors.toList());

        result.put("features", intersectingFeatures);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Geometry createGeometryFromMap(Map<String, Object> geometryMap, GeometryFactory factory) {
        String type = (String) geometryMap.get("type");
        List<?> coordinates = (List<?>) geometryMap.get("coordinates");

        if ("Polygon".equalsIgnoreCase(type)) {
            List<List<List<Double>>> polygonCoords = (List<List<List<Double>>>) coordinates;
            return createPolygon(polygonCoords, factory);
        } else if ("MultiPolygon".equalsIgnoreCase(type)) {
            List<List<List<List<Double>>>> multiPolygonCoords = (List<List<List<List<Double>>>>) coordinates;
            Polygon[] polygons = multiPolygonCoords.stream()
                    .map(polygonCoords -> createPolygon(polygonCoords, factory))
                    .toArray(Polygon[]::new);
            return factory.createMultiPolygon(polygons);
        }
        return null;
    }

    private Polygon createPolygon(List<List<List<Double>>> polygonCoords, GeometryFactory factory) {
        List<Double> shellCoords = polygonCoords.get(0).get(0);
        LinearRing shell = factory.createLinearRing(getCoordinates(polygonCoords.get(0)));
        
        LinearRing[] holes = new LinearRing[polygonCoords.size() - 1];
        for (int i = 1; i < polygonCoords.size(); i++) {
            holes[i - 1] = factory.createLinearRing(getCoordinates(polygonCoords.get(i)));
        }
        
        return factory.createPolygon(shell, holes);
    }

    private Coordinate[] getCoordinates(List<List<Double>> coordsList) {
        return coordsList.stream()
                         .map(c -> new Coordinate(c.get(0), c.get(1)))
                         .toArray(Coordinate[]::new);
    }
}