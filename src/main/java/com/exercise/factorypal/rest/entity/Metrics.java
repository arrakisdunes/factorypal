package com.exercise.factorypal.rest.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Metrics {
    private float avg;
    private float max;
    private float min;
}
