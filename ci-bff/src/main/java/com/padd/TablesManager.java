package com.padd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablesManager {

    @Getter
    private final Map<String, List<String>> tables;

    @Getter
    private Map<String, List<String>> alreadyOrderedPersons;

    public TablesManager() {
        // static data for the tables
        tables = new HashMap<>();
        tables.put("1", List.of("Pierre", "Paul", "Jacques", "Jean"));
        tables.put("2", List.of("Marie", "Sophie", "Lucie", "Alice"));
        tables.put("3", List.of("Tom", "Jerry", "Spike", "Tyke"));
        tables.put("4", List.of("Donald", "Daisy", "Huey", "Dewey"));
        tables.put("5", List.of("Mickey", "Minnie", "Goofy", "Pluto"));
        tables.put("6", List.of("Bugs", "Daffy", "Porky", "Elmer"));
        tables.put("7", List.of("Sylvester", "Tweety", "Granny", "Speedy"));
        tables.put("8", List.of("Homer", "Marge", "Bart", "Lisa"));
        tables.put("9", List.of("Peter", "Lois", "Chris", "Meg"));
        tables.put("10", List.of("Stan", "Kyle", "Cartman", "Kenny"));
        tables.put("11", List.of("Fry", "Leela", "Bender", "Zoidberg"));
        tables.put("12", List.of("Rick", "Morty", "Summer", "Beth"));
        tables.put("13", List.of("Fred", "Daphne", "Velma", "Shaggy"));
        tables.put("14", List.of("Scooby", "Scrappy", "Yabba", "Doo"));
        tables.put("15", List.of("Heathcliff", "Riff-Raff", "Sonja", "Cleo"));

        alreadyOrderedPersons = new HashMap<>();
    }

    public void addOrderedPerson(String table, List<String> persons) {
        System.out.println("Current instance: " + this.hashCode());
        // If no entry for this table create it
        if (!alreadyOrderedPersons.containsKey(table)) {
            System.out.println("Adding a new entry for table " + table + " with persons " + persons);
            alreadyOrderedPersons.put(table, persons);
        } else {
            // We need to add the new persons to the existing list only if not already present
            List<String> existingPersons = alreadyOrderedPersons.get(table);
            for (String person : persons) {
                if (!existingPersons.contains(person)) {
                    System.out.println("Adding " + person + " to the list of persons who have already ordered at table " + table);
                    existingPersons.add(person);
                }
            }
        }
    }

    public boolean hasThisPersonAlreadyOrdered(String person, String table) {
        if (alreadyOrderedPersons.containsKey(table)) {
            return alreadyOrderedPersons.get(table).contains(person);
        }
        return false;
    }

    public String tableArrayToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode tablesArray = objectMapper.createArrayNode();

        for (Map.Entry<String, List<String>> entry : tables.entrySet()) {
            ObjectNode tableNode = objectMapper.createObjectNode();
            tableNode.put("tableNumber", Integer.parseInt(entry.getKey()));
            tableNode.putPOJO("people", entry.getValue());
            tablesArray.add(tableNode);
        }

        rootNode.set("tables", tablesArray);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String getOrderersToJson(String tableNumero) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode tableNode = objectMapper.createObjectNode();

        List<String> initialPeople = tables.getOrDefault(tableNumero, List.of());
        List<String> orderedPeople = alreadyOrderedPersons.getOrDefault(tableNumero, List.of());

        boolean allHaveOrdered = orderedPeople.containsAll(initialPeople);

        tableNode.putPOJO("peopleWhichOrdered", orderedPeople);
        tableNode.put("allHaveOrdered", allHaveOrdered);

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tableNode);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String getAllOrderersToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode tablesArray = objectMapper.createArrayNode();

        for (Map.Entry<String, List<String>> entry : alreadyOrderedPersons.entrySet()) {
            ObjectNode tableNode = objectMapper.createObjectNode();
            tableNode.put("tableNumber", Integer.parseInt(entry.getKey()));
            tableNode.putPOJO("peopleWhichOrdered", entry.getValue());
            tablesArray.add(tableNode);
        }

        rootNode.set("tables", tablesArray);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

}
