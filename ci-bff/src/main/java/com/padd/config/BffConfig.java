package com.padd.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class BffConfig {
    @ConfigProperty(name = "dining.url")
    String diningUrl;

    @ConfigProperty(name = "kitchen.url")
    String kitchenUrl;

    @ConfigProperty(name = "menu.url")
    String menuUrl;

    @ConfigProperty(name = "amount.tables")
    int amountTables;

    @ConfigProperty(name = "seats.per.table")
    int seatsPerTable;



}
