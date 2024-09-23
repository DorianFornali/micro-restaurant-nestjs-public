package com.padd.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class KitchenController {
    @GET
    @Path("/preparations")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPreparations() {
        /*

        We just GET HTTP the kitchen to get the state of the preparations for each table
        and we return a list for each table with the time remaining

         */
        return "test";
    }
}
