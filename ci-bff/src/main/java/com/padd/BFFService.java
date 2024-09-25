package com.padd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.padd.bridge.BridgeToService;
import com.padd.bridge.RestaurantService;
import com.padd.config.BffConfig;
import com.padd.lunchState.LunchStateController;
import com.padd.lunchState.MealType;
import com.padd.model.MenuItem;
import com.padd.model.OrderContainer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BFFService {

    @Getter
    private BridgeToService bridgeToService;
    private LunchStateController lunchStateController;
    private BffConfig bffConfig;

    /** Array containing details on the table orders
     * ordersPerTable[0] contains the OrderContainer for table 0
     * */
    public Map<String, OrderContainer> ordersPerTable;

    @Inject
    public BFFService(BffConfig bffConfig) {
        this.bridgeToService = new BridgeToService(bffConfig);
        this.lunchStateController = new LunchStateController(this);
        this.bffConfig = bffConfig;
        ordersPerTable = new HashMap<>();
    }

    public void handleIncomingOrder(String postBody) {
        //TODO! The postbody contains the order details as well the table number
        //TODO! If the map does not contain the table number, create a new OrderContainer and add it to the map
        //TODO! If the map contains the table number, add the menuItems to the OrderContainer list of items and/or
        //TODO! the supplementItems
        //TODO! Additionnally, if there are any drinks in the order, use the bridgeToService to send a post tableOrders
        
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
                List<String> typesToSendToKitchen = lunchStateController.getTypesToSendToKitchen();

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
                }
                else {
                    System.out.println("First order for that table, creating tableOrder by sending body: {\"tableNumber\": " + tableNumber + ", \"customersCount\": 1}");

                    // DEBUG---------

                    System.out.println("SENDING NOT VIA BRIDGE TO SERVICE -------------------");

                    tableOrderID = getTableOrderIDFromHTTPResponse(
                            bridgeToService.httpPost(RestaurantService.DINING, "tableOrders", "{\"tableNumber\": " + tableNumber + ", \"customersCount\": 1}")
                    );
                }

                for (MenuItem item : itemsToSendToKitchen) {
                    System.out.println("Sending to kitchen ITEM: " + item.get_id());
                    System.out.println(item.toPrettyString());

                    // We POST request to tableOrders to add it to the table order
                    String postBodyItem = "{\"menuItem\": \"" + item.get_id() + "\", " +
                            "\"menuItemShortName\": \"" + item.getShortName() + "\", " +
                            "\"howMany\": 1}";

                    // TODO! This body does not seem to work

                    bridgeToService.httpPost(RestaurantService.DINING,
                            "tableOrders/" + tableOrderID,
                            postBodyItem);
                }





                if (orderContainer == null) {
                    /* if the table does not exists, create a new OrderContainer and add it to the Map (ordersPerTable) 
                     * When the OrderContainer passes by the constructor, it will correctly construct the lists ithin OrderContainer 
                    */

                    orderContainer = new OrderContainer(tableNumber, menuItems);
                    ordersPerTable.put(tableNumber, orderContainer);
                    System.out.println("Created new order for table: " + tableNumber);
                }

                else {
                    /* if the table exists, add the menuItems to the OrderContainer list of items and/or the supplementItems */
                    for (MenuItem menuItem : menuItems) {
                        orderContainer.addMenuItem(menuItem);
                    }
                    System.out.println("Updated order for table: " + tableNumber);
                }

                System.out.println("Preparing table order for table: " + tableNumber);

                String response = bridgeToService.httpPost(RestaurantService.DINING,
                        "tableOrders/" + tableOrderID + "/prepare",
                        "");
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
            List<MenuItem> itemsToSendToKitchen = new ArrayList<>();

            for (MenuItem item : orderContainer.getMenuItems()) {
                if (item.getCategory().equals(typeToSendToKitchen)) {
                    itemsToSendToKitchen.add(item);
                    // For each Item to send to the kitchen, we make a post request to /tableOrders/{tableOrderId}
                    // with the item as body in this shape: {"menuItem": item, "menuItemShortName: item.getShortName(), "howMany": 1}

                    // We create the body of the post request
                    String postBody = "{\"menuItem\": " + item.get_id() +
                            ", \"menuItemShortName\": " + item.getShortName() +
                            ", \"howMany\": 1}";

                    System.out.println("Sending item" + item.get_id() + " to kitchen at URL " + RestaurantService.DINING + "tableOrders/" + orderContainer.getAssociatedTableOrderID());

                    bridgeToService.httpPost(RestaurantService.DINING,
                            "tableOrders/" + orderContainer.getAssociatedTableOrderID(),
                            postBody);
                }
            }

            // We can now PREPARE the tableOrder with a post request, no body just the URL
            bridgeToService.httpPost(RestaurantService.DINING,
                    "tableOrders/" + orderContainer.getAssociatedTableOrderID() + "/prepare",
                    "");



        }
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

    private String sendPostRequest(String urlString, String jsonInputString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder response = new StringBuilder();
            while ((bytesRead = is.read(buffer)) != -1) {
                response.append(new String(buffer, 0, bytesRead));
            }
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
