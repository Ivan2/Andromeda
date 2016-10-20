package com.games.andromeda.logic.phases;

import com.games.andromeda.graph.Node;

public class FleetPreparationStrategy extends ListStrategy<Node, Boolean> {

    @Override
    public Boolean handlePhaseEvent(Node input) {
        if (input.getSystemType() == Node.SystemType.FRIENDLY){
            if (results.contains(input)){
                results.remove(input);
                return true;
            }
            if (results.size() <= 3){
                results.add(input);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean applyChanges() {
        if (results.size() != 3) return false;
        // todo send side and new fleet locations
        return true;
    }

    @Override
    public void autoApplyChanges() {
        // todo choose 3-result.size random friendly systems and add them to results
        applyChanges();
    }
}
