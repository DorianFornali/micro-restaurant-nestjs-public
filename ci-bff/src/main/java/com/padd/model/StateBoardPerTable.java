package com.padd.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateBoardPerTable {

    private String tableID;
    private String tableNumber;
    

    public StateBoardPerTable(String tableID, String tableNumber) {
        this.tableID = tableID;
        this.tableNumber = tableNumber;
    }

    public String toString() {
        return "StateBoardPerTable{" +
                "tableID='" + tableID + '\'' +
                ", tableNumber='" + tableNumber + '\'' +
                '}';
    }


}
