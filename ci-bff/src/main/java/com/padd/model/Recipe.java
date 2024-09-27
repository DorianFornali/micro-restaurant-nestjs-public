package com.padd.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recipe {
    /*
    "recipe": {
      "_id": "66f66c724e86bc1cf0dcc30f",
      "shortName": "burrata",
      "post": "COLD_DISH",
      "cookingSteps": [
        "Take burrata",
        "Take mozzarella",
        "Put them togther",
        "Shake",
        "Ok it's finished!"
      ],
      "meanCookingTimeInSec": 16
    },
     */

    private String _id;
    private String shortName;
    private String post;
    private String[] cookingSteps;
    private int meanCookingTimeInSec;

    public Recipe() {}
}
