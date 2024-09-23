package com.padd.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

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

    public OrderContainer(String id, List<MenuItem> menuItems) {
        /* Modified to take just a list as argument and based on the supplement flag populate the corresponding list */
        this.associatedTableOrderID = id;
        this.menuItems = new ArrayList<>();
        this.supplementItems = new ArrayList<>();
        
        if (menuItems != null) {
            for (MenuItem item : menuItems) {
                addMenuItem(item);
            }
        }
    }


    /*
     * Add menu item based on whether it's a supplement or not 
     * @param item The menu item to add
     */
    public void addMenuItem(MenuItem item) {
        if (item.isSupplement()){
            supplementItems.add(item);
        }
        menuItems.add(item);
    }

}
