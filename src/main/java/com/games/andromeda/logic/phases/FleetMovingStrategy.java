package com.games.andromeda.logic.phases;

import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.multiplayer.Client;

//пока только конечная точка
public class FleetMovingStrategy extends ListStrategy<MoveFleetMessage.Move, Void> {

    @Override
    public Void handlePhaseEvent(MoveFleetMessage.Move input) {
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

    @Override
    public String getTextDescription() {
        return "Передвижение флота";
    }
}
