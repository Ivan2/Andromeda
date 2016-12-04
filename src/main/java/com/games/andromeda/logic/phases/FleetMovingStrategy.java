package com.games.andromeda.logic.phases;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.Pair;
import com.games.andromeda.multiplayer.Client;

//пока только конечная точка
public class FleetMovingStrategy extends ListStrategy<Pair<Fleet, Node>, Void> {

    @Override
    public Void handlePhaseEvent(Pair<Fleet, Node> input) {
        results.add(input);
        return null;
    }

    @Override
    public boolean applyChanges() {
        // todo send path to server (and fleet???)
        Client.getInstance().sendMoveFleetMessage(results);
        return true;
    }

    @Override
    public void autoApplyChanges() {
        Client.getInstance().sendMoveFleetMessage(results);
    }
}
