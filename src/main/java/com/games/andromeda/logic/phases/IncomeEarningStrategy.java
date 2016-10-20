package com.games.andromeda.logic.phases;

import com.games.andromeda.logic.GameObject;

public class IncomeEarningStrategy implements HandlingStrategy<Void, Void>{
    private GameObject.Side side;
    @Override
    public void startPhase(GameObject.Side side) {
        this.side = side;
    }

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
