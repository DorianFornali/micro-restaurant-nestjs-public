package com.padd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.padd.bridge.BridgeToService;
import com.padd.bridge.RestaurantService;
import com.padd.config.BffConfig;
import com.padd.lunchState.LunchAdvancementState;
import com.padd.lunchState.MealType;
import com.padd.model.MenuItem;
import com.padd.model.OrderContainer;
import com.padd.model.PreparedItem;
import com.padd.model.StateBoard;
import com.padd.model.StateBoardPerTable;

import io.quarkus.dev.testing.ContinuousTestingSharedStateManager.State;
import io.quarkus.vertx.http.runtime.devmode.Json;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Singleton
public class BFFService {

    @Getter
    private BridgeToService bridgeToService;
    private LunchStateService lunchStateService;
    private BffConfig bffConfig;
    @Getter
    private TablesManager tablesManager;

    /**
     * Array containing details on the table orders
     * ordersPerTable[0] contains the OrderContainer for table 0
     */
    @Getter
    @Setter
    public Map<String, OrderContainer> ordersPerTable;

    @Inject
    public BFFService(BffConfig bffConfig, LunchStateService lunchStateService) {
        this.bridgeToService = new BridgeToService(bffConfig);
        this.bffConfig = bffConfig;
        this.lunchStateService = lunchStateService;
        this.ordersPerTable = new HashMap<>();
        this.tablesManager = new TablesManager();
        System.out.println("BFFService instance created. HashCode: " + this.hashCode());
    }

    public void handleIncomingOrder(String postBody) {
        try {

            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode rootNode = jsonMessageMapper.readTree(postBody);

            String tableNumber = rootNode.get("tableNumber").asText();
            System.out.println("Succesfully retrieved table number: " + tableNumber);

            /* Handle of the menuItems */
            JsonNode menuItemsNode = rootNode.get("menuItems");
            System.out.println("Succesfully retrieved menuItems: " + menuItemsNode);

            JsonNode peopleNode = rootNode.get("people");
            List<String> people = new ArrayList<>();
            for (JsonNode personNode : peopleNode) {
                people.add(personNode.get("name").asText());
                System.out.println("Succesfully retrieved person: " + personNode.get("name").asText());
            }

            List<MenuItem> menuItems = new ArrayList<>();
            List<MenuItem> itemsToSendToKitchen = new ArrayList<>(); // pass the drinks to the next step
            List<String> typesToSendToKitchen = lunchStateService.getTypesToSendToKitchen();

            for (JsonNode itemNode : menuItemsNode) {
                System.out.println(tableNumber + " is ordering: " + itemNode.get("menuItem"));
                MenuItem menuItem = jsonMessageMapper.treeToValue(itemNode.get("menuItem"), MenuItem.class);

                System.out.println("Test: Succesfully retrieved menuItem: " + menuItem);
                menuItems.add(menuItem);

                if (typesToSendToKitchen.contains(menuItem.getCategory())) {
                    itemsToSendToKitchen.add(menuItem);
                }
            }

            // We do our first TableOrders post to get our tableOrderID

            String tableOrderID = "";
            OrderContainer orderContainer = ordersPerTable.get(tableNumber);

            if (orderContainer != null) {
                tableOrderID = orderContainer.getAssociatedTableOrderID();

                System.out.println("Table order already exists, updating entry for table: " + tableNumber);
                for (MenuItem menuItem : menuItems) {
                    System.out.println("Adding item to order container of table: " + tableNumber + " item: " + menuItem.toPrettyString());
                    orderContainer.addMenuItem(menuItem);
                }
                System.out.println("Updated entry for table: " + tableNumber);
            } else {
                System.out.println("First order for that table, creating tableOrder by sending body: {\"tableNumber\": " + tableNumber + ", \"customersCount\": 1}");

                // DEBUG---------
                tableOrderID = getTableOrderIDFromHTTPResponse(
                        bridgeToService.httpPost(RestaurantService.DINING, "tableOrders", "{\"tableNumber\": " + tableNumber + ", \"customersCount\": 1}")
                );

                orderContainer = new OrderContainer(tableOrderID, menuItems);
                ordersPerTable.put(tableNumber, orderContainer);
                System.out.println("Orders per table updated: " + ordersPerTable.get(tableNumber).getSupplementItems());
                System.out.println("Created new entry for table: " + tableNumber);
            }

            for (MenuItem item : itemsToSendToKitchen) {
                sendMenuItemToTableOrder(tableOrderID, item);
            }

            sendTableOrderToKitchen(tableOrderID);
            try {
                new Thread(() -> {
                    try {
                        System.out.println("Starting cooking process for eligible dishes ...");
                        startCooking(Integer.parseInt(tableNumber));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Finally the tables logic, we add the people to the table in the tablesManager
            tablesManager.addOrderedPerson(tableNumber, people);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Occurs when we advance one step in the lunch, sends the corresponding
     * (already ordered) dishes to the kitchen. For instance, if we are at
     * the STARTER state, we send the starters and the drinks to the kitchen.
     */
    public void sendDishesToKitchen(String typeToSendToKitchen) {
        // We iterate over the map, for each table we will make a post /tableOrders where we put the
        // items of the typeToSendToKitchen

        for (Map.Entry<String, OrderContainer> entry : ordersPerTable.entrySet()) {
            OrderContainer orderContainer = entry.getValue();

            for (MenuItem item : orderContainer.getMenuItems()) {
                int index = LunchAdvancementState.valueOf(typeToSendToKitchen).ordinal();
                if (item.getCategory().equals(MealType.values()[index].toString())) {
                    // We send the item to the tableOrder
                    sendMenuItemToTableOrder(orderContainer.getAssociatedTableOrderID(), item);
                }
            }

            // We can now PREPARE the tableOrder with a post request, no body just the URL
            System.out.println("About to send table order to kitchen for tableOrderID: " + orderContainer.getAssociatedTableOrderID());
            sendTableOrderToKitchen(orderContainer.getAssociatedTableOrderID());

            // And we start the cooking process
            System.out.println("STARTING COOKING PROCESS FOR TABLE: " + entry.getKey() + " ...");

            try {
                new Thread(() -> {
                    try {
                        System.out.println("Starting cooking process for eligible dishes ...");
                        startCooking(Integer.parseInt(entry.getKey()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMenuItemToTableOrder(String tableOrderID, MenuItem item) {
        String postBodyItem = "{\"menuItemId\": \"" + item.get_id() + "\", " +
                "\"menuItemShortName\": \"" + item.getShortName() + "\", " +
                "\"howMany\":" + 1 + "}";

        System.out.println("Inserting item" + item.toPrettyString() + " into tableOrder of id " + tableOrderID + " at URL " + "tableOrders/" + tableOrderID);

        bridgeToService.httpPost(RestaurantService.DINING,
                "tableOrders/" + tableOrderID,
                postBodyItem);
    }

    private void sendTableOrderToKitchen(String tableOrderID) {
        System.out.println("Preparing table order for tableOrderID: " + tableOrderID);
        bridgeToService.httpPost(RestaurantService.DINING,
                "tableOrders/" + tableOrderID + "/prepare",
                "");
    }

    public String getTableOrderId(String numeroTable) {
        return ordersPerTable.get(numeroTable).getAssociatedTableOrderID();
    }

    public String getTableOrderIDFromHTTPResponse(String response) {
        try {
            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode rootNode = jsonMessageMapper.readTree(response);
            System.out.println("Mapping ID from response json, id:" + rootNode.get("_id").asText());

            return rootNode.get("_id").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startCooking(int tableNumber) {
        List<PreparedItem> preparedItems = new ArrayList<>();

        try {

            // Modified flow: we get the tableOrders/tableorderID, iterate over all the preparations
            // and call get /preparedItems/preparedItemID to get the preparedItem, if has not been started yet
            // We add it to the list of preparedItems


            System.out.println("GET request to /tableOrders/" + ordersPerTable.get(String.valueOf(tableNumber)).getAssociatedTableOrderID());
            String tableOrderID = ordersPerTable.get(String.valueOf(tableNumber)).getAssociatedTableOrderID();
            String tableOrderResponse = bridgeToService.httpGet(RestaurantService.DINING, "tableOrders/" + tableOrderID);
            System.out.println("Response from /tableOrders/" + tableOrderID + ": " + tableOrderResponse);


            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode rootNode = jsonMessageMapper.readTree(tableOrderResponse);

            JsonNode preparationsNode = rootNode.get("preparations");

            if (preparationsNode != null) {
                for (JsonNode preparationNode : preparationsNode) {
                    if (preparationNode != null && preparationNode.has("preparedItems")) {
                        JsonNode preparedItemsNode = preparationNode.get("preparedItems");
                        for (JsonNode itemNode : preparedItemsNode) {
                            if (itemNode != null) {
                                // At this state we must get the preparedItemID
                                // We will create the preparedItem based on a request to the kitchen
                                String preparedItemID = itemNode.get("_id").asText();
                                System.out.println("Sending GET request to /preparedItems/" + preparedItemID);
                                String preparedItemResponse = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparedItems/" + preparedItemID);
                                JsonNode preparedItemRootNode = jsonMessageMapper.readTree(preparedItemResponse);

                                PreparedItem preparedItem = jsonMessageMapper.treeToValue(preparedItemRootNode, PreparedItem.class);
                                if (preparedItem.getFinishedAt() == null) {
                                    preparedItems.add(preparedItem);
                                    System.out.println("Found preparedItem not started yet for table: " + tableNumber + " item: " + preparedItem.getShortName());
                                }
                            }
                        }
                    }
                }
            }


            // We create a thread for each preparedItem
            List<Thread> threads = new ArrayList<>();
            for (PreparedItem item : preparedItems) {
                if (item.getFinishedAt() == null) {
                    Thread thread = new Thread(() -> {
                        System.out.println("Inner thread created to handle item: " + item.getShortName() + " for table: " + tableNumber);
                        try {
                            // We start the preparation
                            System.out.println("Sending /start for item: " + item.getShortName() + " for table: " + tableNumber);
                            bridgeToService.httpPost(RestaurantService.KITCHEN, "preparedItems/" + item.get_id() + "/start", "");

                            // We wait for a random time, between 5 and 10 seconds
                            int randomTime = (int) (Math.random() * 5) + 5;
                            Thread.sleep(randomTime * 1000L);

                            // We finish the preparation
                            System.out.println("Sending /finish for item: " + item.getShortName() + " for table: " + tableNumber);
                            bridgeToService.httpPost(RestaurantService.KITCHEN, "preparedItems/" + item.get_id() + "/finish", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    threads.add(thread);
                    thread.start();
                }
            }

            // We wait for all threads to finish
            for (Thread thread : threads) {
                System.out.println("Waiting for thread" + thread.hashCode() + " to finish...");
                thread.join();
                System.out.println("Thread" + thread.hashCode() + " has finished!");
            }

            System.out.println("All threads have finished, at this point all dishes should be ready to be served for table: " + tableNumber + " now sleeping 20s");

            Thread.sleep(20000);
            System.out.println("Now /takenToTable for table: " + tableNumber);

            // We call the /takenToTable endpoint on every preparation
            // We first get the preparations for the table at /preparations?state=readyToBeServed

            String readyToBeServedResponse = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparations?state=readyToBeServed");
            // We fetch the _id field for every preparation in the response

            JsonNode readyToBeServedRootNode = jsonMessageMapper.readTree(readyToBeServedResponse);
            List<String> preparationIds = new ArrayList<>();
            for (JsonNode itemNode : readyToBeServedRootNode) {
                preparationIds.add(itemNode.get("_id").asText());
            }

            // We call the /takenToTable endpoint on every preparation
            for (String preparationId : preparationIds) {
                System.out.println("Taking preparation with id: " + preparationId + " to the table ...");
                bridgeToService.httpPost(RestaurantService.KITCHEN, "preparations/" + preparationId + "/takenToTable", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public StateBoard getStateBoard() {
        StateBoard stateBoard = new StateBoard();
    
        String startedTables = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparations?state=preparationStarted");
        String preparedTables = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparations?state=readyToBeServed");
        try {
            ObjectMapper jsonMessageMapper = new ObjectMapper();
            List<StateBoardPerTable> startedTablesList = processTables(jsonMessageMapper, startedTables);
            List<StateBoardPerTable> readyToBeServedList = processTables(jsonMessageMapper, preparedTables);
    
            stateBoard.setPreparationStarted(startedTablesList);
            stateBoard.setReadyToBeServed(readyToBeServedList);
            return stateBoard;
        } 
        
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    private List<StateBoardPerTable> processTables(ObjectMapper jsonMessageMapper, String tables) throws IOException {
        List<StateBoardPerTable> preparations = new ArrayList<>();
        JsonNode rootNode = jsonMessageMapper.readTree(tables);

        for (JsonNode itemNode : rootNode) {
            preparations.add(new StateBoardPerTable(itemNode.get("_id").asText(), itemNode.get("tableNumber").asText()));
        }
    
        return preparations;
    }
}

