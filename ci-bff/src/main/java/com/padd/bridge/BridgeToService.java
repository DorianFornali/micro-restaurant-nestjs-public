package com.padd.bridge;


import com.padd.config.BffConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
public class BridgeToService {

    BffConfig bffConfig;

    @Inject
    public BridgeToService(BffConfig bffConfig){
        System.out.println("Creating bridge to service");
        this.bffConfig = bffConfig;
    }

    public String httpGet(RestaurantService restaurantService, String resourceTarget){
        HttpURLConnection conn;
        switch (restaurantService) {
            case DINING:
                conn = initConnection(restaurantService, resourceTarget);
                if(conn == null){
                    System.err.println("Connection to + " + restaurantService + " failed");
                }
                else {
                    return receiveAnswer(conn);
                }
            break;


            case KITCHEN:
                conn = initConnection(restaurantService, resourceTarget);
                if(conn == null){
                    System.err.println("Connection to + " + restaurantService + " failed");
                }
                else {
                    return receiveAnswer(conn);
                }
            break;


            case MENU:
                conn = initConnection(restaurantService, resourceTarget);
                if(conn == null){
                    System.err.println("Connection to + " + restaurantService + " failed");
                }
                else {
                    return receiveAnswer(conn);
                }

                return null;
        }
        return "Failed to get resource at " + restaurantService + "/" + resourceTarget;
    }

    public String httpPost(RestaurantService restaurantService, String resourceTarget, String body){
        HttpURLConnection conn;
        switch(restaurantService){
            case DINING :
                conn = initConnection(restaurantService, resourceTarget);
                if(conn == null){
                    System.err.println("Connection to + " + restaurantService + " failed");
                }
                else {
                    return receiveAnswer(conn, body);
                }
            break;
            case KITCHEN :
                conn = initConnection(restaurantService, resourceTarget);
                if(conn == null){
                    System.err.println("Connection to + " + restaurantService + " failed");
                }
                else {
                    return receiveAnswer(conn, body);
                }
            break;
            case MENU :
                conn = initConnection(restaurantService, resourceTarget);
                if(conn == null){
                    System.err.println("Connection to + " + restaurantService + " failed");
                }
                else {
                    return receiveAnswer(conn, body);
                }

            break;
        }

        return "Failed to get resource at " + restaurantService + "/" + resourceTarget;
    }

    private HttpURLConnection initConnection(RestaurantService restaurantService, String resourceTarget){
        try {
            switch (restaurantService) {
                case DINING:
                    URI diningUrl = new URI(bffConfig.getDiningUrl() + "/" + resourceTarget);
                    return (HttpURLConnection) diningUrl.toURL().openConnection();
                case KITCHEN:
                    URI kitchenUrl = new URI(bffConfig.getKitchenUrl() + "/" + resourceTarget);
                    return (HttpURLConnection) kitchenUrl.toURL().openConnection();
                case MENU:
                    URI menuUrl = new URI(bffConfig.getMenuUrl() + "/" + resourceTarget);
                    return (HttpURLConnection) menuUrl.toURL().openConnection();
                default:
                    return null;
            }
        } catch(URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String receiveAnswer(HttpURLConnection conn){
        try {
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = is.read(buffer);
            StringBuilder response = new StringBuilder();
            while (bytesRead != -1) {
                response.append(new String(buffer, 0, bytesRead));
                bytesRead = is.read(buffer);
            }
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String receiveAnswer(HttpURLConnection conn, String body){
        try {
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/json");
            conn.getOutputStream().write(body.getBytes());
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = is.read(buffer);
            StringBuilder response = new StringBuilder();
            while (bytesRead != -1) {
                response.append(new String(buffer, 0, bytesRead));
                bytesRead = is.read(buffer);
            }
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
