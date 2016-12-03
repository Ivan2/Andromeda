package com.games.andromeda.logic.phases;


import com.games.andromeda.multiplayer.Client;

public class IncomeEarningStrategy extends CommonHandlingStrategy<Void, Void>{
    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }

    @Override
    public boolean applyChanges() {
        // todo get money from bases and increase player's pocket
        int delta = 10;
        Client.getInstance().sendPocketChangesMessage(delta);
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
