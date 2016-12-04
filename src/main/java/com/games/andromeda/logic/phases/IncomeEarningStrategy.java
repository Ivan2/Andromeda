package com.games.andromeda.logic.phases;


import com.games.andromeda.Phases;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;

public class IncomeEarningStrategy extends CommonHandlingStrategy<Void, Void>{
    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }

    @Override
    public boolean applyChanges() {
        // todo get money from bases and increase player's pocket
        WorldAccessor.getInstance().getPocket(Phases.getInstance().side).increase(100);
        Client.getInstance().sendPocketChangesMessage(
                WorldAccessor.getInstance().getPocket(Phases.getInstance().side).getTotal());
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
