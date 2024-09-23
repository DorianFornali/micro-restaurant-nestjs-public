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
        //TODO! HTTP GET to the kitchen service
        return "test";
    }
}
