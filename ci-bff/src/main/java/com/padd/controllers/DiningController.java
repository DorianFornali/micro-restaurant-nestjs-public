package com.padd.controllers;

import com.padd.BFFService;
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
    public Response getTableOrder(@PathParam("numTable") int numTable) {
        /*
        Ici on cherche a obtenir tous les supplements pour une table donnee
        On retourne simplement la liste des supplements stockées pour la table
        Aucun appel au backend
         */
        // Retrieve supplements for the given table number
        return Response.ok("Supplements for table " + numTable).build();
    }

    @POST
    @Path("/supplements/{numTable}/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postTableOrderPay(@PathParam("numTable") int numTable, String paymentDetails) {
        /*
        Paiement pour les supplements d'une table, pas forcmeent tous:
        On recoit une liste de menuItems et on les enleve de la liste des supplements
        PUIS check sur la liste des supplements, si elle est vide tout a ete reglé, donc
        a ce moment la on fait un appel /tableOrders/{id}/bill pour cloturer la commande
         */
        // Process the paymentDetails here
        return Response.ok("Payment processed for table " + numTable).build();
    }
}