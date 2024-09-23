package com.padd.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public class MenuController {
    @GET
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMenu() {
        //TODO! HTTP GET to the menu service
        return "test";
    }

    @POST
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean postMenuItem() {
        //TODO! HTTP POST to the menu service returns TRUE if successful
        return true;
    }

    @GET
    @Path("/menus/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMenuItem(@PathParam("id") int id) {
        //TODO! HTTP GET to the menu service
        return "test";
    }
}
