package com.padd.controllers;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.padd.BFFService;
import com.padd.bridge.RestaurantService;
import com.padd.model.MenuItem;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dining")
public class DiningController {

    // ------------ Command flow related endpoints ------------


    private BFFService bffService;

    @Inject
    public DiningController(BFFService bffService) {
        this.bffService = bffService;
    }

    // need to test with curl 
    @POST
    @Path("/newOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postNewOrder(String orderDetails) {
        bffService.handleIncomingOrder(orderDetails);
        return Response.ok("Order received").build();
    }

    // ------------ Payment flow related endpoints ------------

    @GET
    @Path("/supplements/{numTable}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTableOrder(@PathParam("numTable") String numTable) {
        /*
        Ici on cherche a obtenir tous les supplements pour une table donnee
        On retourne simplement la liste des supplements stockées pour la table
        Aucun appel au backend
         */
        // Retrieve supplements for the given table number

        List<MenuItem> supplementsForTable = bffService.ordersPerTable.get(numTable).getSupplementItems();
        
        // Convert the list to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(supplementsForTable);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting supplements to JSON").build();
        }
        
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
                // TODO - call the bill endpoint
                bffService.getBridgeToService().httpPost(RestaurantService.DINING, "/tableOrders/" + bffService.getTableOrderId(numTable) + "/bill", "");

            }
        }

        catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error processing payment").build();
        }


        return Response.ok("Payment processed for table " + numTable).build();
    }
}