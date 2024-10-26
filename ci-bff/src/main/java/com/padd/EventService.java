package com.padd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padd.bridge.BridgeToService;
import com.padd.bridge.RestaurantService;
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

    private BridgeToService bridgeToService;

    @Inject
    public EventService(BridgeToService bridgeToService) {
        events = new HashMap<>();
        this.bridgeToService = bridgeToService;
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

    public String getAllEvents(){
        // Create the json object corresponding to all the events and returns it
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /** Will build the menu for the event based on the menu items ids it has
     * Has to make one call per menu item id to the backend
     * @return A json object corresponding to the event's menu */
    public String getEventMenu(String eventName) {
        Event event = events.get(eventName);
        List<String> menu = event.getMenu();
        List<JsonNode> menuItems = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String menuItemId : menu) {
            try {
                System.out.println("Fetching menu item with id " + menuItemId + " from backend");
                String response = bridgeToService.httpGet(RestaurantService.MENU, "menus/" + menuItemId);
                JsonNode menuItemNode = objectMapper.readTree(response);
                menuItems.add(menuItemNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(menuItems);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public boolean eventExists(String eventName){
        return events.containsKey(eventName);
    }
}
