package com.padd.bridge;


import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

@ApplicationScoped
public class BridgeToService {

    @ConfigProperty(name = "dining.url")
    URI diningUrl;

    @ConfigProperty(name = "kitchen.url")
    URI kitchenUrl;

    @ConfigProperty(name = "menu.url")
    URI menuUrl;

    public BridgeToService(){
        System.out.println("Creating bridge to service");
    }

    public String httpGet(RestaurantService restaurantService){
        return "";
    }

    public boolean httpPost(){
        return true;
    }

}
