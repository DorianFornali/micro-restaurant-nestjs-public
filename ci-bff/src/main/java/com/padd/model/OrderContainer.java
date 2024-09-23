package com.padd.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderContainer {

    /** The ID of the table order associated */
    @Getter
    @Setter
    private String associatedTableOrderID;

    /** The list of products ordered for the table */
    @Getter
    private List<MenuItem> menuItems;

    /** The list of supplements ordered for the table */
    @Getter
    private List<MenuItem> supplementItems;

    public OrderContainer(String id, List<MenuItem> menuItems, List<MenuItem> supplementItems) {
        this.associatedTableOrderID = id;
        this.menuItems = menuItems;
        this.supplementItems = supplementItems;
    }

}
