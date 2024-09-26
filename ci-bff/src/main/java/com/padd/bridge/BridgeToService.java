package com.padd.bridge;

import com.padd.config.BffConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

@ApplicationScoped
public class BridgeToService {

    BffConfig bffConfig;

    public BridgeToService(BffConfig bffConfig) {
        System.out.println("Creating bridge to service");
        this.bffConfig = bffConfig;
    }

    public String httpGet(RestaurantService restaurantService, String resourceTarget) {
        HttpURLConnection conn;
        switch (restaurantService) {
            case DINING:
            case KITCHEN:
            case MENU:
                conn = initConnection(restaurantService, resourceTarget);
                if (conn == null) {
                    System.err.println("Connection to " + restaurantService + " failed");
                } else {
                    return receiveAnswer(conn);
                }
                break;
        }
        return "Failed to get resource at " + restaurantService + "/" + resourceTarget;
    }

    public String httpPost(RestaurantService restaurantService, String resourceTarget, String body) {
        String urlString = getUrlString(restaurantService, resourceTarget);
        if (urlString == null) {
            System.err.println("Connection to " + restaurantService + " failed");
            return "Failed to get resource at " + restaurantService + "/" + resourceTarget;
        }
        return sendPostRequest(urlString, body);
    }

    private HttpURLConnection initConnection(RestaurantService restaurantService, String resourceTarget) {
        try {
            URI uri = new URI(getUrlString(restaurantService, resourceTarget));
            return (HttpURLConnection) uri.toURL().openConnection();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getUrlString(RestaurantService restaurantService, String resourceTarget) {
        switch (restaurantService) {
            case DINING:
                return bffConfig.getDiningUrl() + "/" + resourceTarget;
            case KITCHEN:
                return bffConfig.getKitchenUrl() + "/" + resourceTarget;
            case MENU:
                return bffConfig.getMenuUrl() + "/" + resourceTarget;
            default:
                return null;
        }
    }

    private String receiveAnswer(HttpURLConnection conn) {
        try {
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

            int statusCode = conn.getResponseCode();
            InputStream is;
            if (statusCode >= 200 && statusCode < 300) {
                is = conn.getInputStream();
            } else {
                System.out.println("Seems like an error occurred: "
                        + conn.getResponseMessage() +
                        " when sending POST request to "
                        + urlString +
                        " with body: "
                        + jsonInputString);
                is = conn.getErrorStream();
            }

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