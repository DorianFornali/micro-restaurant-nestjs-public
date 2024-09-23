package com.padd.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/menu")
public class MenuController {

    @GET
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMenu() {
        /*
        Ici on effectue un simple GET HTTP au menu service et on renvoie ce qu'on recoit
        */
        // Implement the logic to get the menu here
        return Response.ok("Menu list").build();
    }

    @POST
    @Path("/menus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMenuItem(String menuItem) {
        /*
        On ajoute un item a la carte
        Ici on effectue un simple POST HTTP au menu service et on renvoie ce qu'on recoit
        */
        // Implement the logic to post a new menu item here
        return Response.ok("Menu item added").build();
    }

    @GET
    @Path("/menus/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMenuItem(@PathParam("id") int id) {
        /*
        Simple appel GET HTTP au menu service pour obtenir un item de la carte
         */
        // Implement the logic to get a specific menu item here
        return Response.ok("Menu item with ID: " + id).build();
    }
}