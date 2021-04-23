package com.exercise.factorypal.rest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class MetricsPair {
    @JsonProperty("line_id")
    private long lineId;
    private Metrics metrics;
}
