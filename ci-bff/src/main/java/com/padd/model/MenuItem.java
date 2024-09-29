package com.padd.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItem {

    public MenuItem () {} 

    public MenuItem(String _id, String fullName, String shortName, double price, String category, String image, boolean supplement) {
        this._id = _id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.price = price;
        this.category = category;
        this.image = image;
        this.supplement = supplement;
    }

    private String _id;
    private String fullName;
    private String shortName;
    private double price;
    private String category;
    private String image;
    private boolean supplement;

    public String toPrettyString() {
        return "MenuItem{" +
                "_id='" + _id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                ", isSupplement=" + supplement +
                '}';
    }
}
