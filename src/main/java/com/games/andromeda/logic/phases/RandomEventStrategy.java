package com.games.andromeda.logic.phases;


import com.games.andromeda.logic.GameObject;

public class RandomEventStrategy implements HandlingStrategy<Void, Void> {
    @Override
    public void startPhase(GameObject.Side side) {

    }

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }


    @Override
    public boolean applyChanges() {
        // todo сотворить какую-то рандомную дичь
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }
}
