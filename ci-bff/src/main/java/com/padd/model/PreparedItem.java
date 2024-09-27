package com.padd.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreparedItem {

    private String _id;
    private String shortName;
    private Recipe recipe;
    private String shouldStartAt;
    private String startedAt;
    private String finishedAt;

}
