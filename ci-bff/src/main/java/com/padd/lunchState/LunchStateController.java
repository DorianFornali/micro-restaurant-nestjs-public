package com.padd.lunchState;

import com.padd.BFFService;
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
@ApplicationScoped
public class LunchStateController {

    @Getter
    @Setter
    private LunchAdvancementState currentLunchState = LunchAdvancementState.DRINKS_ONLY;

    BFFService bffService;

    public LunchStateController(BFFService bffService) {
        this.bffService = bffService;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/advance")
    public Response advanceLunchState(String desiredLunchState) {
        int n = LunchAdvancementState.values().length;

        System.out.println("Received order to advance to state: " + LunchAdvancementState.values()[Integer.parseInt(desiredLunchState)]);

        int targetStateIndex = Integer.parseInt(desiredLunchState);

        if(targetStateIndex < n){
            this.setCurrentLunchState(LunchAdvancementState.values()[targetStateIndex]);
            System.out.println("Set current state: " + this.currentLunchState);
            bffService.sendDishesToKitchen(this.currentLunchState.toString());

            // Return a successful response
            return Response.ok("Successfully advanced to state: " + this.currentLunchState).build();
        }
        else {
            System.out.println("Invalid state index");

            // Return an error response
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid state index: " + targetStateIndex)
                    .build();
        }
    }

    public List<String> getTypesToSendToKitchen(){
        // We get the index o the current state within the list of possible values for the state
        int n = LunchAdvancementState.values().length;
        int index = currentLunchState.ordinal();

        if(index == 0){
            List<String> result = new ArrayList<>();
            result.add(MealType.BEVERAGE.toString());
            return result;
        }

        System.out.println("Current state index: " + index);
        System.out.println("Maximum length: " + n);

        // We create a list of the types of dishes to send to the kitchen
        List<String> typesToSendToKitchen = new ArrayList<>();
        // We add the types of dishes to send to the kitchen
        for (int i = 0; i < index; i++) {
            typesToSendToKitchen.add(MealType.values()[i].toString());
            System.out.println("i: "+ i);
        }

        return typesToSendToKitchen;
    }

}
