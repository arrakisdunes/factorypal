package com.exercise.factorypal.rest;

import com.exercise.factorypal.rest.entity.LineSpeedRequest;
import com.exercise.factorypal.rest.entity.Metrics;
import com.exercise.factorypal.rest.entity.MetricsPair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Api(value = "Root REST API")
@RestController
@RequestMapping(value = "/rest")
public class LineSpeedService {

    private static final Logger LOG = LoggerFactory.getLogger(LineSpeedService.class);

    private HashMap<Long, List<LineSpeedRequest>> data = new HashMap<>();

    @Value("#{'${app.known.lines}'.split(',')}")
    private List<Long> listKnownLines;

    @Value("${app.max.minutes:60}")
    private int maxMinutes;

    @PostConstruct
    private void postConstruct() {
        //Startup the lines
        listKnownLines.forEach(p-> data.put(p, Collections.synchronizedList(new ArrayList<LineSpeedRequest>())));
    }

    @ApiOperation(value = "Save line speed")
    @PostMapping(value = "/linespeed", consumes = "application/json")
    @ResponseBody
    public HttpEntity saveLineSpeed(@RequestBody LineSpeedRequest request) {
        LOG.info("Line entered: "+request.toString());
        long anHourLess = getAnHourLessTimestamp();

        //Reject if hour is bigger than 60m
        if(request.getTimestamp() < anHourLess){
            LOG.warn("Line entered is to old: "+request.getTimestamp());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        //Reject if not found
        if(!data.containsKey(request.getLineId())){
            LOG.warn("Line entered is not allowed: "+request.getLineId());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        addLineSpeedMap(request);
        //OK
        return new ResponseEntity(HttpStatus.OK);
    }

    private void addLineSpeedMap(LineSpeedRequest r){
        List<LineSpeedRequest> l = data.get(r.getLineId());
        l.add(r);

        //sort all the elements
        Collections.sort(l);
        data.put(r.getLineId(), l);
    }

    @ApiOperation(value = "Get the metrics")
    @GetMapping(value = "/metrics/{lineid}")
    @ResponseBody
    public HttpEntity<Metrics> getMetrics(@PathParam("lineid") long lineid) {
        LOG.info("Get metrics for line: "+lineid);

        if(!data.containsKey(lineid)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Metrics metrics = null;
        try {
            metrics = calcMetrics(lineid);
        } catch (Exception e){
            //Log this
            LOG.error("Internal Error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOG.debug("Metrics generated: "+ metrics.toString());
        return new HttpEntity<Metrics>(metrics);
    }

    private Metrics calcMetrics(long lineid) {
        long anHourLess = getAnHourLessTimestamp();
        Metrics metrics = new Metrics();
        List<LineSpeedRequest> lastHour = data.get(lineid);
        if (Objects.nonNull(lastHour)) {
            AtomicReference<Float> total = new AtomicReference<>((float) 0);
            AtomicInteger size = new AtomicInteger();
            lastHour.stream().filter(p -> p.getTimestamp() >= anHourLess).forEach(p -> {
                size.getAndIncrement();
                total.updateAndGet(v -> (float) (v + p.getSpeed()));
                if (metrics.getMin() == 0 || metrics.getMin() > p.getSpeed() && p.getSpeed() > 0) {
                    metrics.setMin(p.getSpeed());
                }

                if (metrics.getMax() < p.getSpeed()) {
                    metrics.setMax(p.getSpeed());
                }
            });
            if (size.get() > 0) {
                metrics.setAvg(total.get() / size.get());
            }
        }

        return metrics;
    }

    @ApiOperation(value = "Get the metrics")
    @GetMapping(value = "/metrics/")
    @ResponseBody
    public HttpEntity<List<MetricsPair>> getMetrics() {
        List<MetricsPair> list = new ArrayList<>();
        data.forEach((k,v)-> {
            if(v.size()>0){
                MetricsPair pair = new MetricsPair(k, this.calcMetrics(k));
                pair.setLineId(k);
                list.add(pair);
            }
        });

        return new HttpEntity<List<MetricsPair>>(list);
    }

    private long getAnHourLessTimestamp() {
        return Instant.now().minus(maxMinutes, ChronoUnit.MINUTES).toEpochMilli();
    }
}
