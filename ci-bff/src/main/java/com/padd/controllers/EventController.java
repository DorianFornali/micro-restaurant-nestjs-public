package com.padd.controllers;

import com.padd.EventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/event")
public class EventController {

    @Inject
    EventService eventService;

    @Path("/create")
    public Response createEvent(String postBody) {
        eventService.handleEventOperation(postBody);
        return Response.ok("Event successfully created").build();
    }
}
