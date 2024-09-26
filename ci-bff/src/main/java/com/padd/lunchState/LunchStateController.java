package com.padd.lunchState;

import com.padd.BFFService;
import com.padd.LunchStateService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Path("/lunchState")
public class LunchStateController {



    BFFService bffService;
    LunchStateService lunchStateService;


    @Inject
    public LunchStateController(BFFService bffService, LunchStateService lunchStateService) {
        this.bffService = bffService;
        this.lunchStateService = lunchStateService;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/advance")
    public Response advanceLunchState(String desiredLunchState) {
        return lunchStateService.advanceLunchState(desiredLunchState, bffService);
    }

}
