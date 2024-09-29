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

    /** Array containing details on the table orders
     * ordersPerTable[0] contains the OrderContainer for table 0
     * */
    @Getter
    @Setter
    public Map<String, OrderContainer> ordersPerTable;

    @Inject
    public BFFService(BffConfig bffConfig, LunchStateService lunchStateService) {
        this.bridgeToService = new BridgeToService(bffConfig);
        this.bffConfig = bffConfig;
        this.lunchStateService = lunchStateService;
        this.ordersPerTable = new HashMap<>();
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

                if(orderContainer != null){
                    tableOrderID = orderContainer.getAssociatedTableOrderID();

                    System.out.println("Table order already exists, updating entry for table: " + tableNumber);
                    for (MenuItem menuItem : menuItems) {
                        System.out.println("Adding item to order container of table: " + tableNumber + " item: " + menuItem.toPrettyString());
                        orderContainer.addMenuItem(menuItem);
                    }
                    System.out.println("Updated entry for table: " + tableNumber);
                }
                else {
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
            }

            catch (Exception e){
                e.printStackTrace();
            }
    }

    /** Occurs when we advance one step in the lunch, sends the corresponding
     * (already ordered) dishes to the kitchen. For instance, if we are at
     * the STARTER state, we send the starters and the drinks to the kitchen.
     * */
    public void sendDishesToKitchen(String typeToSendToKitchen){
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
            startCooking(Integer.parseInt(entry.getKey()));

        }
    }

    private void sendMenuItemToTableOrder(String tableOrderID, MenuItem item){
        String postBodyItem = "{\"menuItemId\": \"" + item.get_id() + "\", " +
                "\"menuItemShortName\": \"" + item.getShortName() + "\", " +
                "\"howMany\":" + 1 + "}";

        System.out.println("Inserting item" + item.toPrettyString() + " into tableOrder of id " + tableOrderID + " at URL " + "tableOrders/" + tableOrderID);

        bridgeToService.httpPost(RestaurantService.DINING,
                "tableOrders/" + tableOrderID,
                postBodyItem);
    }

    private void sendTableOrderToKitchen(String tableOrderID){
        System.out.println("Preparing table order for tableOrderID: " + tableOrderID);
        bridgeToService.httpPost(RestaurantService.DINING,
                "tableOrders/" + tableOrderID + "/prepare",
                "");
    }

    public String getTableOrderId(String numeroTable){
        return ordersPerTable.get(numeroTable).getAssociatedTableOrderID();
    }

    public String getTableOrderIDFromHTTPResponse(String response){
        try {
            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode rootNode = jsonMessageMapper.readTree(response);
            System.out.println("Mapping ID from response json, id:" + rootNode.get("_id").asText());

            return rootNode.get("_id").asText();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void startCooking(int tableNumber){
        List<PreparedItem> preparedItems = new ArrayList<>();

        System.out.println("Trying to /start cooking for table: " + tableNumber);


        try {
            System.out.println("Calling the kitchen to get prepared items for HOT DISHES ...");
            String hotDishResponse = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparedItems?post=HOT_DISH");
            System.out.println("Calling the kitchen to get prepared items for COLD DISHES ...");
            String coldDishResponse = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparedItems?post=COLD_DISH");
            System.out.println("Calling the kitchen to get prepared items for BAR ...");
            String barResponse = bridgeToService.httpGet(RestaurantService.KITCHEN, "preparedItems?post=BAR");

            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode hotDishRootNode = jsonMessageMapper.readTree(hotDishResponse);
            JsonNode coldDishRootNode = jsonMessageMapper.readTree(coldDishResponse);
            JsonNode barRootNode = jsonMessageMapper.readTree(barResponse);

            for (JsonNode itemNode : hotDishRootNode) {
                PreparedItem preparedItem = jsonMessageMapper.treeToValue(itemNode, PreparedItem.class);
                preparedItems.add(preparedItem);
            }

            for (JsonNode itemNode : coldDishRootNode) {
                PreparedItem preparedItem = jsonMessageMapper.treeToValue(itemNode, PreparedItem.class);
                preparedItems.add(preparedItem);
            }

            for (JsonNode itemNode : barRootNode) {
                PreparedItem preparedItem = jsonMessageMapper.treeToValue(itemNode, PreparedItem.class);
                preparedItems.add(preparedItem);
            }

            for(PreparedItem item : preparedItems){
                System.out.println("Found following Prepared item: " + item.getShortName());
            }

            System.out.println("Now starting the cooking process...");

            // We create a thread for each preparedItem
            List<Thread> threads = new ArrayList<>();
            for (PreparedItem item : preparedItems) {
                if (item.getFinishedAt() == null) {
                    System.out.println("Item " + item.getShortName() + " has finishedAt field to null ...");
                    Thread thread = new Thread(() -> {
                        try {
                            // We start the preparation
                            System.out.println("Starting preparation for item: " + item.getShortName());
                            bridgeToService.httpPost(RestaurantService.KITCHEN, "preparedItems/" + item.get_id() + "/start", "");

                            // We wait for a random time, between 5 and 10 seconds
                            int randomTime = (int) (Math.random() * 5) + 5;
                            Thread.sleep(randomTime * 1000L);

                            // We finish the preparation
                            System.out.println("Finishing preparation for item: " + item.getShortName());
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
            }

            System.out.println("All threads have finished, all dishes have been cooked! :");

            new Thread(() -> {
                try {
                    System.out.println("Thread sleeping 20s before taking dishes to table ...");
                    Thread.sleep(20000);
                    System.out.println("Now trying to take the dishes of that table to the table ...");

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

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            }).start();

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

