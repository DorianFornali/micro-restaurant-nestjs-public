package com.padd.model.event;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class Event {
    @Getter
    @Setter
    private String eventName;

    @Getter
    private String date;

    @Getter
    private Map<String, List<String>> tables;

    @Getter
    private List<String> menu;

    public Event(String eventName, String date, Map<String, List<String>> tables, List<String> menu) {
        this.eventName = eventName;
        this.date = date;
        this.tables = tables;
        this.menu = menu;
    }
}
