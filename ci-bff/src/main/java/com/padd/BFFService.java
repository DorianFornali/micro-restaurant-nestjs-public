package com.padd;

import com.padd.bridge.BridgeToService;
import com.padd.config.BffConfig;
import com.padd.model.OrderContainer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BFFService {

    BridgeToService bridgeToService;
    BffConfig bffConfig;

    /** Array containing details on the table orders
     * ordersPerTable[0] contains the OrderContainer for table 0
     * */
    Map<Integer, OrderContainer> ordersPerTable;

    @Inject
    public BFFService(BridgeToService bridgeToService, BffConfig bffConfig) {
        this.bridgeToService = bridgeToService;
        this.bffConfig = bffConfig;
        ordersPerTable = new HashMap<>();
    }

    public void handleIncomingOrder(String postBody) {
        //TODO! The postbody contains the order details as well as well as the table number
        //TODO! If the map does not contain the table number, create a new OrderContainer and add it to the map
        //TODO! If the map contains the table number, add the menuItems to the OrderContainer list of items and/or
        //TODO! the supplementItems
        //TODO! Additionnally, if there are any drinks in the order, use the bridgeToService to send a post tableOrders

    }






}
