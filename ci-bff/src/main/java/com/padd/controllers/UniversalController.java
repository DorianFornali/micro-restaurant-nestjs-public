package com.padd.controllers;

import com.padd.model.OrderContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padd.BFFService;
import com.padd.bridge.RestaurantService;
import com.padd.model.MenuItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Path("/bff")
public class UniversalController {
    private final BFFService bffService;

    @Inject
    public UniversalController(BFFService bffService) {
        this.bffService = bffService;
        System.out.println("BFFService HashCode in UniversalController Constructor: " + bffService.hashCode());
    }

    // -----------------------------------------------------------------------------------
    // Endpoints aiming at targeting the menu service --------------------------------
    // -----------------------------------------------------------------------------------

    @GET
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMenu() {
        System.out.println("Request received on BFF to GET menus");
        return Response.ok(bffService.getBridgeToService().httpGet(RestaurantService.MENU, "menus")).build();

    }

    @POST
    @Path("/menus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMenuItem(String menuItem) {
        return(Response.ok(bffService.getBridgeToService().
                httpPost(RestaurantService.MENU, "menus", menuItem)).build());

    }

    @GET
    @Path("/menus/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMenuItem(@PathParam("id") String id) {
        System.out.println("Request received on BFF to GET menu item with id " + id);
        return Response.ok(bffService.getBridgeToService().httpGet(RestaurantService.MENU, "menus/" + id)).build();
    }

    // -----------------------------------------------------------------------------------
    // Endpoints aiming at targeting the dining service --------------------------------
    // -----------------------------------------------------------------------------------

    @POST
    @Path("/newOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postNewOrder(String orderDetails) {
        bffService.handleIncomingOrder(orderDetails);
        return Response.ok("Order received and treated").build();
    }

    // ------------ Payment flow related endpoints ------------

    @GET
    @Path("/supplements/{numTable}")
    @Produces(MediaType.APPLICATION_JSON)  // Use JSON since you're returning a JSON array
    public Response getTableOrder(@PathParam("numTable") String numTable) {
        System.out.println("Checking the supplements for table " + numTable);
        System.out.println("Retrieving supplements. BFFService HashCode: " + this.bffService.hashCode());

        // Retrieve the OrderContainer for the table
        OrderContainer orderContainer = bffService.ordersPerTable.get(numTable);

        // Check if order exists for the table
        if (orderContainer == null) {
            System.out.println("No order found for table " + numTable);
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("No order found for table " + numTable)
                           .build();
        }

        // Get the supplement items
        List<MenuItem> supplementsForTable = orderContainer.getSupplementItems();

        // If no supplements are available, return an empty list
        if (supplementsForTable == null || supplementsForTable.isEmpty()) {
            System.out.println("No supplements found for table " + numTable);
            return Response.ok("[]").build();  // Return empty JSON array
        }

        // Convert the list of supplements to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(supplementsForTable);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error converting supplements to JSON")
                           .build();
        }

        // Return the list of supplements as a JSON response
        System.out.println("Supplements for table " + numTable + ": " + supplementsForTable);
        return Response.ok(json).build();
}

    @POST
    @Path("/supplements/{numTable}/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postTableOrderPay(@PathParam("numTable") String numTable, String paymentDetails) {
        /*
        Paiement pour les supplements d'une table, pas forcmeent tous:
        On recoit une liste de menuItems et on les enleve de la liste des supplements
        PUIS check sur la liste des supplements, si elle est vide tout a ete reglé, donc
        a ce moment la on fait un appel /tableOrders/{id}/bill pour cloturer la commande
         */
        // Process the paymentDetails here
        try {
            Map<String, OrderContainer> ordersPerTable = bffService.ordersPerTable;
            System.out.println(ordersPerTable);
            List<MenuItem> supplementItems = bffService.ordersPerTable.get(numTable).getSupplementItems();
            ObjectMapper jsonMessageMapper = new ObjectMapper();
            JsonNode rootNode = jsonMessageMapper.readTree(paymentDetails);
            JsonNode menuItemsNode = rootNode.get("supplementItmes"); // TODO - adapt the key to the actual key in the json
            for (JsonNode itemNode : menuItemsNode) {
                MenuItem menuItem = jsonMessageMapper.treeToValue(itemNode.get("menuItem"), MenuItem.class);
                supplementItems.remove(menuItem);
            }
            bffService.ordersPerTable.get(numTable).setSupplementItems(supplementItems);
            if (supplementItems.isEmpty()) {
                bffService.getBridgeToService().httpPost(RestaurantService.DINING, "/tableOrders/" + bffService.getTableOrderId(numTable) + "/bill", "");

            }
        }
        catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error processing payment").build();
        }
        return Response.ok("Payment processed for table " + numTable).build();
    }


    // -----------------------------------------------------------------------------------
    // Endpoints aiming at targeting the kitchen service --------------------------------
    // -----------------------------------------------------------------------------------

    @GET
    @Path("/preparations")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPreparations() {
        /*
        We just GET HTTP the kitchen to get the state of the preparations for each table
        and we return a list for each table with the time remaining
        */
        return Response.ok(bffService.getStateBoard().toString()).build();
    }
}
