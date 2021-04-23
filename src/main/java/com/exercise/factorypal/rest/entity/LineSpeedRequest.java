package com.exercise.factorypal.rest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LineSpeedRequest implements Comparable<LineSpeedRequest> {
    @JsonProperty("line_id")
    private long lineId;
    private float speed;
    private long timestamp;

    @Override
    public int compareTo(LineSpeedRequest o) {
        return  Long.compare(this.getTimestamp(), o.getTimestamp());
    }
}
