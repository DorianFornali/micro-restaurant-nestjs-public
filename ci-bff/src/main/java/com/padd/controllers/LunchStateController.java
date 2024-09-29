package com.padd.controllers;

import com.padd.BFFService;
import com.padd.LunchStateService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/lunchState")
public class LunchStateController {


    @Inject
    BFFService bffService;
    @Inject
    LunchStateService lunchStateService;

    public LunchStateController() {
        System.out.println("BFFService HashCode in LunchStateController Constructor: " + bffService.hashCode());
    }


    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/advance")
    public Response advanceLunchState(String desiredLunchState) {
        return lunchStateService.advanceLunchState(desiredLunchState, bffService);
    }

    @GET
    @Path("/")
    public Response getCurrentLunchState() {
        return Response.ok(lunchStateService.getCurrentLunchState().toString()).build();
    }

}
