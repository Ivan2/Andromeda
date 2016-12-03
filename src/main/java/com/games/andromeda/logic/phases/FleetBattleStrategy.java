package com.games.andromeda.logic.phases;

import com.games.andromeda.multiplayer.Client;

public class FleetBattleStrategy extends CommonHandlingStrategy<Void, Void> {

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    } // nothing depends on player

    @Override
    public boolean applyChanges() {
        // todo show battle, fix fleet changes
        Client.getInstance().sendEndFightMessage();
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
