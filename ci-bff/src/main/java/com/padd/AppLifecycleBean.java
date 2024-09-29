package com.padd;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class AppLifecycleBean {
    @Inject
    BFFService bffService;

    void onStart(@Observes StartupEvent ev) {
        System.out.println("App started. BFFService HashCode: " + bffService.hashCode());
    }
}
