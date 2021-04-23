package com.exercise.factorypal.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exercise.factorypal.rest.entity.LineSpeedRequest;
import com.exercise.factorypal.rest.entity.Metrics;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LineSpeedServiceTest {
    @Test
    public void testSaveLineSpeed() {
        LineSpeedService lineSpeedService = new LineSpeedService();
        HttpEntity actualSaveLineSpeedResult = lineSpeedService.saveLineSpeed(new LineSpeedRequest());
        assertNull(actualSaveLineSpeedResult.getBody());
        assertEquals("<204 NO_CONTENT No Content,[]>", actualSaveLineSpeedResult.toString());
        assertEquals(HttpStatus.NO_CONTENT, ((ResponseEntity) actualSaveLineSpeedResult).getStatusCode());
    }

    @Test
    public void testSaveLineSpeed2() {
        LineSpeedService lineSpeedService = new LineSpeedService();

        LineSpeedRequest lineSpeedRequest = new LineSpeedRequest();
        lineSpeedRequest.setSpeed(10.0f);
        HttpEntity actualSaveLineSpeedResult = lineSpeedService.saveLineSpeed(lineSpeedRequest);
        assertNull(actualSaveLineSpeedResult.getBody());
        assertEquals("<204 NO_CONTENT No Content,[]>", actualSaveLineSpeedResult.toString());
        assertEquals(HttpStatus.NO_CONTENT, ((ResponseEntity) actualSaveLineSpeedResult).getStatusCode());
    }

    @Test
    public void testSaveLineSpeed3() {
        LineSpeedService lineSpeedService = new LineSpeedService();

        LineSpeedRequest lineSpeedRequest = new LineSpeedRequest();
        lineSpeedRequest.setTimestamp(Long.MAX_VALUE);
        HttpEntity actualSaveLineSpeedResult = lineSpeedService.saveLineSpeed(lineSpeedRequest);
        assertNull(actualSaveLineSpeedResult.getBody());
        assertEquals("<404 NOT_FOUND Not Found,[]>", actualSaveLineSpeedResult.toString());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseEntity) actualSaveLineSpeedResult).getStatusCode());
    }

    @Test
    public void testGetMetrics() {
        HttpEntity<Metrics> actualMetrics = (new LineSpeedService()).getMetrics(1L);
        assertNull(actualMetrics.getBody());
        assertEquals("<404 NOT_FOUND Not Found,[]>", actualMetrics.toString());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseEntity<Metrics>) actualMetrics).getStatusCode());
    }
}

