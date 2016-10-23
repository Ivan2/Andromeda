package com.games.andromeda.logic.phases;


public class IncomeEarningStrategy extends CommonHandlingStrategy<Void, Void>{
    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }

    @Override
    public boolean applyChanges() {
        // todo get money from bases and increase player's pocket
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
