package com.padd;

import com.padd.lunchState.LunchAdvancementState;
import com.padd.lunchState.MealType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LunchStateService {

    @Getter
    @Setter
    private LunchAdvancementState currentLunchState = LunchAdvancementState.DRINKS_ONLY;
    @Inject
    public LunchStateService() {
    }

    public Response advanceLunchState(String desiredLunchState, BFFService bffService) {
        int n = LunchAdvancementState.values().length;
        int targetStateIndex = Integer.parseInt(desiredLunchState);

        // Check if the index is invalid at the beginning
        if(targetStateIndex >= n){
            System.out.println("Invalid state index");

            // Return an error response
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid state index: " + targetStateIndex + ". The maximum index is: " + (n - 1) + " corresponding to the DESSERTS phase.")
                    .build();
        }

        System.out.println("Received order to advance to state: " + LunchAdvancementState.values()[targetStateIndex]);

        this.setCurrentLunchState(LunchAdvancementState.values()[targetStateIndex]);
        System.out.println("Set current state: " + this.currentLunchState);
        bffService.sendDishesToKitchen(this.currentLunchState.toString());

        // Return a successful response
        return Response.ok("Successfully advanced to state: " + this.currentLunchState).build();
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

        // We create a list of the types of dishes to send to the kitchen
        List<String> typesToSendToKitchen = new ArrayList<>();
        // We add the types of dishes to send to the kitchen
        for (int i = 0; i <= index; i++) {
            typesToSendToKitchen.add(MealType.values()[i].toString());
            System.out.println("i: "+ i);
        }

        return typesToSendToKitchen;
    }

}
