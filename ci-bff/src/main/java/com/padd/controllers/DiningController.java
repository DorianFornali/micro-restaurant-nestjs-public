package com.padd.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public class DiningController {

    @POST
    @Path("/newOrder")
    @Produces(MediaType.TEXT_PLAIN)
    public String postNewOrder() {
        /*

        The body contains the list of commanded items for a given table numero.
        These items will be marked as supplements or not.
        We will store Map<NumeroTable, (idTableOrder + List<Produits> + supplements)
        At receive, we store the tagged suplements in the supplements list, and remove
        these fields from the products.
        If we see any drink we put them in the tableOrder == http post /tableOrders with
        the table number and the list of drinks.
        --> This will create the preparations etc


         */
        return "";
    }



    @GET
    @Path("/supplements/{numTable}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTableOrder(@PathParam("numTable") int numTable) {
        /*

        Ici on cherche a obtenir tous les supplements pour une table donnee
        On retourne simplement la liste des supplements stockées pour la table
        Aucun appel au backend

         */
        return "test";
    }

    @POST
    @Path("/supplements/{numTable}/pay")
    @Produces(MediaType.TEXT_PLAIN)
    public String postTableOrderPay(@PathParam("numTable") int numTable) {
        /*

        Paiement pour les supplements d'une table, pas forcmeent tous:
        On recoit une liste de menuItems et on les enleve de la liste des supplements
        PUIS check sur la liste des supplements, si elle est vide tout a ete reglé, donc
        a ce moment la on fait un appel /tableOrders/{id}/bill pour cloturer la commande

         */
        return "";
    }

}
