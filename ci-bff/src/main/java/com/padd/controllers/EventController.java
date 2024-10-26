package com.padd.controllers;

import com.padd.EventService;
import com.padd.model.event.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/event")
public class EventController {

    @Inject
    EventService eventService;

    @Path("/create")
    @POST
    @Consumes("application/json")
    public Response createEvent(String postBody) {
        System.out.println("Received event creation request");
        eventService.handleEventOperation(postBody);
        return Response.ok("Event successfully created").build();
    }

    @Path("/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam("name") String eventName) {
        String event = eventService.getEvent(eventName);
        if (event != null) {
            return Response.ok(event).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Event not found").build();
        }
    }

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEvents() {
        String events = eventService.getAllEvents();
        if (events != null) {
            return Response.ok(events).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No events found").build();
        }
    }

    @Path("/{name}/menu")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventMenu(@PathParam("name") String eventName) {
        System.out.println("Received request for event menu");
        if(!eventService.eventExists(eventName)){
            return Response.status(Response.Status.NOT_FOUND).entity("Event not found").build();
        }

        String menu = eventService.getEventMenu(eventName);
        if (menu != null) {
            return Response.ok(menu).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Menu not found").build();
        }
    }
}
