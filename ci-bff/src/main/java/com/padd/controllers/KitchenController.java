package com.padd.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/kitchen")
public class KitchenController {

    @GET
    @Path("/preparations")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPreparations() {
        /*
        We just GET HTTP the kitchen to get the state of the preparations for each table
        and we return a list for each table with the time remaining
         */
        // Implement the logic to get the preparations here
        return Response.ok("Preparations list").build();
    }
}