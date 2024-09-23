package com.padd.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public class DiningController {
    @POST
    @Path("/tableOrders")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean postTableOrder() {
        //TODO! HTTP POST to the dining service returns TRUE if successful
        return true;
    }


    /** To get the supplements at the payment flow*/
    @GET
    @Path("/tableOrders/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTableOrders(@PathParam("id") int id) {
        //TODO! HTTP GET to the dining service
        return "test";
    }

    @POST
    @Path("/tableOrders/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean postTableOrder(@PathParam("id") int id) {
        //TODO! HTTP POST to the dining service returns TRUE if successful
        return true;
    }

    @POST
    @Path("/tableOrders/{id}/bill")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean postTableOrderBill(@PathParam("id") int id) {
        //TODO! HTTP POST to the dining id bill service returns TRUE if successful
        return true;
    }
}
