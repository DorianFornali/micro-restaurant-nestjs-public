package com.padd.controllers;

import com.padd.BFFService;
import com.padd.bridge.RestaurantService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bff")
public class UniversalController {

    BFFService bffService;

    @Inject
    public UniversalController(BFFService bffService) {
        this.bffService = bffService;
    }

    @GET
    @Path("/menus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMenu() {
        System.out.println("Request received on BFF to GET menus");
        return Response.ok(bffService.getBridgeToService().httpGet(RestaurantService.MENU, "menus")).build();

    }

    @POST
    @Path("/menus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMenuItem(String menuItem) {
        return(Response.ok(bffService.getBridgeToService().
                httpPost(RestaurantService.MENU, "menus", menuItem)).build());

    }

    @GET
    @Path("/menus/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMenuItem(@PathParam("id") String id) {
        return Response.ok(bffService.getBridgeToService().httpGet(RestaurantService.MENU, "menus/" + id)).build();
    }
}
