package com.padd.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItem {

    public MenuItem () {} 

    public MenuItem(String _id, String fullName, String shortName, double price, String category, String image, boolean isSupplement) {
        this._id = _id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.price = price;
        this.category = category;
        this.image = image;
        this.isSupplement = isSupplement;
    }

    private String _id;
    private String fullName;
    private String shortName;
    private double price;
    private String category;
    private String image;
    private boolean isSupplement;

    public String toPrettyString() {
        return "MenuItem{" +
                "_id='" + _id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                ", isSupplement=" + isSupplement +
                '}';
    }
}
