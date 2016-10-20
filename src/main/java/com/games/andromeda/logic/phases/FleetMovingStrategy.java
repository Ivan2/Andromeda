package com.games.andromeda.logic.phases;

import com.games.andromeda.graph.Node;

public class FleetMovingStrategy extends ListStrategy<Node, Void> {
    @Override
    public Void handlePhaseEvent(Node input){
        results.add(input);
        return null;
    }

    @Override
    public boolean applyChanges() {
        // todo send path to server (and fleet???)
        return true;
    }

    @Override
    public void autoApplyChanges() {

    }
}
