package com.games.andromeda.logic.phases;

import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.multiplayer.Client;

//пока только конечная точка
public class FleetMovingStrategy extends CommonHandlingStrategy<MoveFleetMessage.Move, Void> {

    @Override
    public Void handlePhaseEvent(MoveFleetMessage.Move input) {
        Client.getInstance().sendMoveFleetMessage(input);
        return null;
    }

    @Override
    public boolean applyChanges() {
        // todo refactor message name
        Client.getInstance().sendEndFightMessage();
        //sendMoveFleetMessage(results);
        return true;
    }

    @Override
    public void autoApplyChanges() {
       applyChanges();
    }

    @Override
    public String getTextDescription() {
        return "Передвижение флота";
    }
}
