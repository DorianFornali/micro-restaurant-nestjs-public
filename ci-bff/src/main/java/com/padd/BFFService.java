package com.padd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.padd.bridge.BridgeToService;
import com.padd.config.BffConfig;
import com.padd.model.MenuItem;
import com.padd.model.OrderContainer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BFFService {

    @Getter
    private BridgeToService bridgeToService;
    private BffConfig bffConfig;

    /** Array containing details on the table orders
     * ordersPerTable[0] contains the OrderContainer for table 0
     * */
    public Map<String, OrderContainer> ordersPerTable;

    @Inject
    public BFFService(BffConfig bffConfig) {
        this.bridgeToService = new BridgeToService(bffConfig);
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
                List<MenuItem> drinksItems = new ArrayList<>(); // pass the drinks to the next step 
                for (JsonNode itemNode : menuItemsNode) {
                    System.out.println(tableNumber + " is ordering: " + itemNode.get("menuItem"));
                    MenuItem menuItem = jsonMessageMapper.treeToValue(itemNode.get("menuItem"), MenuItem.class);
                    
                    System.out.println("Test: Succesfully retrieved menuItem: " + menuItem);
                    menuItems.add(menuItem);
                    if (menuItem.getCategory().equals("Drinks")) {
                        drinksItems.add(menuItem);
                    }
                }

                OrderContainer orderContainer = ordersPerTable.get(tableNumber);
                
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
            }

            catch (Exception e){
                e.printStackTrace();
            }
    }

    public String getTableOrderId(String numeroTable){
        return ordersPerTable.get(numeroTable).getAssociatedTableOrderID();
    }

}
