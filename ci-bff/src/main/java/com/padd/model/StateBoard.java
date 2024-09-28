package com.padd.model;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class StateBoard {
    private List<StateBoardPerTable> preparationStarted;
    private List<StateBoardPerTable> readyToBeServed;

    public StateBoard() {
        this.preparationStarted = new ArrayList<StateBoardPerTable>();
        this.readyToBeServed = new ArrayList<StateBoardPerTable>();  
    }
    @Override
    public String toString() {
        return "StateBoard{" +
            "preparationStarted=" + preparationStarted +
            ", readyToBeServed=" + readyToBeServed +
            '}';
    }
}
