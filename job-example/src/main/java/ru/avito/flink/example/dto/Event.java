package ru.avito.flink.example.dto;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    @JsonProperty("id")
    private Integer eventId;
    private Integer uid;
    private String ip;
    private Integer itemId;
    private Long timestamp;

    public Event() {
    }

    public Event(Integer eventId, Integer uid, String ip, Integer itemId, Long timestamp) {
        this.eventId = eventId;
        this.uid = uid;
        this.ip = ip;
        this.itemId = itemId;
        this.timestamp = timestamp;
    }

    public Integer getEventId() {
        return eventId;
    }

    public Integer getUid() {
        return uid;
    }

    public String getIp() {
        return ip;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
