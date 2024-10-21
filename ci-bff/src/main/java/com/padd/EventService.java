package com.padd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padd.model.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class EventService {

    private Map<String, Event> events = new HashMap<>();

    @Inject
    public EventService(){
        events = new HashMap<>();
    }

    public void handleEventOperation(String body){
        try {
            // We parse the body (json object)
            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode rootNode = jsonMessageMapper.readTree(body);

            // First we get the event name
            String eventName = rootNode.get("name").asText();

            System.out.println("Creating or modifying event: " + eventName);

            // Then we get the date
            String date = rootNode.get("date").asText();

            // Then we get the tables with the persons
            Map<String, List<String>> tables = new HashMap<>();
            JsonNode tablesNode = rootNode.get("tables");
            for (JsonNode tableNode : tablesNode){
                String tableID = tableNode.get("tableID").asText();
                List<String> persons = new ArrayList<>();
                for (JsonNode personNode : tableNode.get("persons")){
                    persons.add(personNode.asText());
                }
                tables.put(tableID, persons);
            }

            // Then we get the menu, which is a list of menuItemID
            List<String> menu = new ArrayList<>();
            JsonNode menuNode = rootNode.get("menu");
            for (JsonNode menuItemNode : menuNode){
                menu.add(menuItemNode.asText());
            }

            // Based on these information we create an Event object that we store in the local map
            Event event = new Event(eventName, date, tables, menu);
            events.put(eventName, event);

            System.out.println("Event " + eventName + " created or modified");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getEvent(String eventName){
        // Create the json object corresponding to the event and returns it
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(events.get(eventName));
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
