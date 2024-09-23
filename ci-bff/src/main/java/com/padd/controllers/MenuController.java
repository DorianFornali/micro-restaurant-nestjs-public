package com.padd.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public class MenuController {
    @GET
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMenu() {
        /*

        Ici on effectue un simple GET HTTP au menu service et on renvoie ce qu'on recoit
        */
        return "test";
    }

    @POST
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public String postMenuItem() {
        /*

        On ajoute un item a la carte
        Ici on effectue un simple POST HTTP au menu service et on renvoie ce qu'on recoit

        */


        return "";
    }

    @GET
    @Path("/menus/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMenuItem(@PathParam("id") int id) {

        /*

        Simple appel GET HTTP au menu service pour obtenir un item de la carte

         */
        return "test";
    }
}
