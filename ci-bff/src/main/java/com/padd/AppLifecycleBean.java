package com.padd;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class AppLifecycleBean {
    @Inject
    BFFService bffService;

    @Inject
    EventService eventService;

    void onStart(@Observes StartupEvent ev) {
        System.out.println("App started. BFFService HashCode: " + bffService.hashCode());
        System.out.println("App started. EventService HashCode: " + eventService.hashCode());
    }
}
